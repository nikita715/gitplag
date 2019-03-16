package ru.nikstep.redink.git.loader

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.auth.AuthorizationService
import ru.nikstep.redink.util.sendRestRequest

/**
 * Loader of files from Github
 */
class GithubLoader(
    solutionStorage: SolutionStorage,
    repositoryRepository: RepositoryRepository,
    private val authorizationService: AuthorizationService
) : AbstractGitLoader(solutionStorage, repositoryRepository) {

    private val logger = KotlinLogging.logger {}

    override fun loadFileText(repoFullName: String, branchName: String, fileName: String, secretKey: String): String =
        sendRestRequest("https://raw.githubusercontent.com/$repoFullName/$branchName/$fileName")

    override fun loadChangedFiles(pullRequest: PullRequest): List<String> =
        pullRequest.run {
            sendRestRequest<JsonArray<*>>(
                "https://api.github.com/repos/$mainRepoFullName/pulls/$number/files",
                accessToken = authorizationService.getAuthorizationToken(secretKey)
            ).mapNotNull {
                val fileRecord = it as JsonObject
                if (isChanged(fileRecord)) fileRecord.string("filename") else null
            }
        }

    fun isChanged(changedFileRecord: JsonObject) =
        changedFileRecord.string("status") == "added" ||
                changedFileRecord.string("status") == "modified"
}
