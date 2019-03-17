package ru.nikstep.redink.git.loader

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import net.lingala.zip4j.core.ZipFile
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.SourceFileInfo
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.SourceCode
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.auth.AuthorizationService
import ru.nikstep.redink.util.inTempDirectory
import ru.nikstep.redink.util.sendRestRequest
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL

/**
 * Loader of files from Github
 */
class GithubLoader(
    private val solutionStorage: SolutionStorage,
    repositoryRepository: RepositoryRepository,
    private val authorizationService: AuthorizationService
) : AbstractGitLoader(solutionStorage, repositoryRepository) {

    private val logger = KotlinLogging.logger {}

    override fun loadFilesOfRepository(repo: Repository): List<SourceCode> {
        val branches = mutableSetOf<String>()
        val toList = sendRestRequest<JsonArray<JsonObject>>("https://api.github.com/repos/${repo.name}/pulls")
            .flatMap {
                val prNumber = requireNotNull(it.int("number"))
                val sourceRepoName = requireNotNull(it.obj("head")?.obj("repo")?.string("full_name"))
                val sourceBranchName = requireNotNull(it.obj("head")?.string("ref"))

                branches += sourceBranchName

                loadChangedFiles(repo.name, prNumber).map { fileName ->
                    val fileText = loadFileText(sourceRepoName, sourceBranchName, fileName, "")
                    solutionStorage.saveSolution(
                        SourceFileInfo(
                            gitService = GitProperty.GITHUB,
                            sourceBranchName = sourceBranchName,
                            mainRepoFullName = repo.name,
                            prNumber = prNumber,
                            fileName = fileName,
                            fileText = fileText,
                            creator = requireNotNull(it.obj("head")?.obj("user")?.string("login")),
                            mainBranchName = requireNotNull(it.obj("base")?.string("ref")),
                            headSha = requireNotNull(it.obj("head")?.string("sha"))
                        )
                    )
                }
            }

        loadBases(branches, repo)
        return toList
    }

    private fun loadBases(
        branches: MutableSet<String>,
        repo: Repository
    ) {
        inTempDirectory { tempDir ->
            branches.forEach { branch ->
                val zipFile = File("$tempDir/zip.zip")
                BufferedInputStream(URL("https://github.com/${repo.name}/archive/$branch.zip").openStream())
                    .use { inputStream ->
                        val bytes = inputStream.readBytes()
                        FileOutputStream(zipFile).use {
                            it.write(bytes)
                            ZipFile(zipFile).extractAll(tempDir)
                        }

                    }
                solutionStorage.saveBases(
                    "$tempDir/${repo.name.substringAfter("/")}-$branch",
                    repo.gitService, repo.name, branch
                )
            }
        }
    }

    override fun loadFileText(repoFullName: String, branchName: String, fileName: String, secretKey: String): String =
        sendRestRequest("https://raw.githubusercontent.com/$repoFullName/$branchName/$fileName")

    override fun loadChangedFiles(pullRequest: PullRequest): List<String> =
        loadChangedFiles(pullRequest.mainRepoFullName, pullRequest.number)

    private fun loadChangedFiles(mainRepoFullName: String, prNumber: Int): List<String> =
        sendRestRequest<JsonArray<*>>(
            "https://api.github.com/repos/$mainRepoFullName/pulls/$prNumber/files"
        ).mapNotNull {
            val fileRecord = it as JsonObject
            if (isChanged(fileRecord)) fileRecord.string("filename") else null
        }

    fun isChanged(changedFileRecord: JsonObject) =
        changedFileRecord.string("status") == "added" ||
                changedFileRecord.string("status") == "modified"
}
