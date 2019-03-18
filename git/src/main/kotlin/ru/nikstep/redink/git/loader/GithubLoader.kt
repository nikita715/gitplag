package ru.nikstep.redink.git.loader

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.SourceFileInfo
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.SolutionFileRecord
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.downloadAndUnpackZip
import ru.nikstep.redink.util.sendRestRequest

/**
 * Loader of files from Github
 */
class GithubLoader(
    private val solutionStorage: SolutionStorage
) : AbstractGitLoader(solutionStorage) {

    override fun loadFilesOfPullRequest(pullRequest: PullRequest) {
        downloadAndUnpackZip("https://github.com/${pullRequest.sourceRepoFullName}/archive/${pullRequest.sourceBranchName}.zip") { unpackedDir ->
        }
    }

    private val logger = KotlinLogging.logger {}

    fun downloadBase(repo: Repository, branchName: String, fileName: String) {
        val fileText = loadFileText(repo.name, branchName, fileName)
        solutionStorage.saveBaseByText(repo, branchName, fileName, fileText)
    }

    override fun loadRepositoryAndPullRequestFiles(repo: Repository): List<SolutionFileRecord> {
        val branches = mutableSetOf<String>()
        val toList = sendRestRequest<JsonArray<JsonObject>>("https://api.github.com/repos/${repo.name}/pulls")
            .flatMap {
                val prNumber = requireNotNull(it.int("number"))
                val sourceRepoName = requireNotNull(it.obj("head")?.obj("repo")?.string("full_name"))
                val sourceBranchName = requireNotNull(it.obj("head")?.string("ref"))

                branches += sourceBranchName

                loadChangedFiles(repo.name, prNumber).map { fileName ->
                    val fileText = loadFileText(sourceRepoName, sourceBranchName, fileName)
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

    fun loadBases(branches: Set<String>, repo: Repository) {
        branches.forEach { branch ->
            downloadAndUnpackZip("https://github.com/${repo.name}/archive/$branch.zip") { unpackedDir ->
                solutionStorage.saveBasesFromDir(
                    "$unpackedDir/${repo.name.substringAfter("/")}-$branch",
                    repo, branch
                )
            }
        }
    }

    override fun loadFileText(repoFullName: String, branchName: String, fileName: String): String =
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

    override fun loadChangedFilesOfCommit(repoName: String, headSha: String): List<String> =
        sendRestRequest<JsonObject>(
            "https://api.github.com/repos/$repoName/commits/$headSha"
        ).array<JsonObject>("files")!!.mapNotNull {
            val fileRecord = it
            if (isChanged(fileRecord)) fileRecord.string("filename") else null
        }

    fun isChanged(changedFileRecord: JsonObject) =
        changedFileRecord.string("status") == "added" ||
                changedFileRecord.string("status") == "modified"
}
