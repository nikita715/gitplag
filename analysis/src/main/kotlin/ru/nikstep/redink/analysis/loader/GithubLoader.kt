package ru.nikstep.redink.analysis.loader

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.github.kittinunf.fuel.core.Method
import mu.KotlinLogging
import ru.nikstep.redink.analysis.AnalysisException
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

    private val githubGraphqlApi = "https://api.github.com/graphql"

    override fun loadFileText(pullRequest: PullRequest, branchName: String, fileName: String): String {
        val fileResponse = sendRestRequest<JsonObject>(
            url = githubGraphqlApi,
            method = Method.POST,
            body = toFileQuery(pullRequest.repoFullName, branchName, fileName),
            accessToken = authorizationService.getAuthorizationToken(pullRequest.secretKey)
        )

        val resultObject = fileResponse.obj("data")?.obj("repository")

        if (resultObject?.get("object") == null)
            throw AnalysisException("$fileName is not found in ${pullRequest.branchName} branch, ${pullRequest.repoFullName} repo")

        return requireNotNull(resultObject.obj("object")?.string("text")) { "fileText is null" }
    }

    override fun loadChangedFiles(pullRequest: PullRequest): List<String> =
        pullRequest.run {
            sendRestRequest<JsonArray<*>>(
                url = "https://api.github.com/repos/$repoFullName/pulls/$number/files",
                accessToken = authorizationService.getAuthorizationToken(secretKey)
            ).map { (it as JsonObject).string("filename")!! }
        }


    fun toFileQuery(repoName: String, branchName: String, fileName: String): String {
        val (owner, file) = repoName.split("/").toTypedArray()
        return """{"query": "query {repository(owner: "$owner", name: "$file")
            | {object(expression: "$branchName:$fileName") {... on Blob{text}}}}"}""".trimMargin()
    }
}
