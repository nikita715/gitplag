package ru.nikstep.redink.analysis.loader

import mu.KotlinLogging
import org.springframework.http.HttpMethod
import ru.nikstep.redink.analysis.AnalysisException
import ru.nikstep.redink.analysis.solutions.SolutionStorageService
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.RequestUtil.Companion.sendGraphqlRequest
import ru.nikstep.redink.util.auth.AuthorizationService
import java.io.File

class GithubServiceLoader(
    private val solutionStorageService: SolutionStorageService,
    private val repositoryRepository: RepositoryRepository,
    private val authorizationService: AuthorizationService
) : GitServiceLoader {

    private val logger = KotlinLogging.logger {}

    private val rawGithubFileQuery = "{\"query\": \"query {repository(owner: \\\"%s\\\", name: \\\"%s\\\")" +
            " {object(expression: \\\"%s:%s\\\") {... on Blob{text}}}}\"}"

    override fun loadFilesFromGit(pullRequest: PullRequest) {

        val fileNames =
            pullRequest.changedFiles.intersect(repositoryRepository.findByName(pullRequest.repoFullName).filePatterns)

        fileNames.map { fileName ->
            checkBaseExists(pullRequest, fileName)

            val args = pullRequest.repoFullName.split("/").toTypedArray()
            val fileResponse = sendGraphqlRequest(
                httpMethod = HttpMethod.POST,
                body = String.format(
                    rawGithubFileQuery,
                    *args,
                    pullRequest.branchName,
                    fileName
                ),
                accessToken = authorizationService.getAuthorizationToken(pullRequest.installationId)
            )

            val resultObject = fileResponse.getJSONObject("data").getJSONObject("repository")

            if (resultObject.isNull("object"))
                throw AnalysisException("$fileName is not found in ${pullRequest.branchName} branch, ${pullRequest.repoFullName} repo")

            solutionStorageService.saveSolution(
                pullRequest, fileName,
                resultObject.getJSONObject("object").getString("text")
            )
        }
    }

    fun getFileQuery(repoName: String, branchName: String, fileName: String): String {
        val args = repoName.split("/").toTypedArray()
        return """{"query": "query {repository(owner: "${args[0]}", name: "${args[1]}")
            | {object(expression: "$branchName:$fileName") {... on Blob{text}}}}"}""".trimMargin()
    }

    private fun checkBaseExists(data: PullRequest, fileName: String) {
        val base = solutionStorageService.loadBase(data.repoFullName, fileName)
        if (base.notExists()) saveBase(data, fileName)
    }

    private fun saveBase(pullRequest: PullRequest, fileName: String) {
        val fileResponse = sendGraphqlRequest(
            httpMethod = HttpMethod.POST,
            body = String.format(
                rawGithubFileQuery,
                *(pullRequest.repoFullName.split("/").toTypedArray()),
                "master",
                fileName
            ),
            accessToken = authorizationService.getAuthorizationToken(pullRequest.installationId)
        )


        val resultObject = fileResponse.getJSONObject("data").getJSONObject("repository")

        if (resultObject.isNull("object"))
            throw AnalysisException("$fileName is not found in ${pullRequest.branchName} branch, ${pullRequest.repoFullName} repo")

        val fileText = resultObject.getJSONObject("object").getString("text")

        solutionStorageService.saveBase(pullRequest, fileName, fileText)
    }
}

private fun File.notExists(): Boolean {
    return !this.exists()
}
