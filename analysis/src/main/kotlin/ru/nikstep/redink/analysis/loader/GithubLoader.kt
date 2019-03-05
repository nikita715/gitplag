package ru.nikstep.redink.analysis.loader

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
    private val solutionStorage: SolutionStorage,
    private val repositoryRepository: RepositoryRepository,
    private val authorizationService: AuthorizationService
) : AbstractGitLoader(solutionStorage, repositoryRepository) {

    private val logger = KotlinLogging.logger {}

    override fun loadFileText(pullRequest: PullRequest, branchName: String, fileName: String): String =
        sendRestRequest(
            url = "https://raw.githubusercontent.com/${pullRequest.repoFullName}/$branchName/$fileName",
            accessToken = authorizationService.getAuthorizationToken(pullRequest.secretKey)
        )

    override fun loadChangedFiles(pullRequest: PullRequest): List<String> =
        pullRequest.run {
            sendRestRequest<JsonArray<*>>(
                url = "https://api.github.com/repos/$repoFullName/pulls/$number/files",
                accessToken = authorizationService.getAuthorizationToken(secretKey)
            ).map { (it as JsonObject).string("filename")!! }
        }
}
