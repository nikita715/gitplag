package ru.nikstep.redink.analysis.loader

import com.github.kittinunf.fuel.core.Method
import mu.KotlinLogging
import ru.nikstep.redink.analysis.AnalysisException
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.auth.AuthorizationService
import ru.nikstep.redink.util.sendGithubGraphqlRequest
import java.io.File

class GithubServiceLoader(
    private val solutionStorage: SolutionStorage,
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

            val fileResponse = sendGithubGraphqlRequest(
                method = Method.POST,
                body = String.format(
                    rawGithubFileQuery,
                    *pullRequest.repoFullName.split("/").toTypedArray(),
                    pullRequest.branchName,
                    fileName
                ),
                accessToken = authorizationService.getAuthorizationToken(pullRequest.installationId)
            )

            val resultObject = fileResponse.obj("data")!!.obj("repository")!!

            if (resultObject["object"] == null)
                throw AnalysisException("$fileName is not found in ${pullRequest.branchName} branch, ${pullRequest.repoFullName} repo")

            solutionStorage.saveSolution(
                pullRequest, fileName,
                resultObject.obj("object")!!.string("text")!!
            )
        }
    }

    fun getFileQuery(repoName: String, branchName: String, fileName: String): String {
        val args = repoName.split("/").toTypedArray()
        return """{"query": "query {repository(owner: "${args[0]}", name: "${args[1]}")
            | {object(expression: "$branchName:$fileName") {... on Blob{text}}}}"}""".trimMargin()
    }

    private fun checkBaseExists(data: PullRequest, fileName: String) {
        val base = solutionStorage.loadBase(data.repoFullName, fileName)
        if (base.notExists()) saveBase(data, fileName)
    }

    private fun saveBase(pullRequest: PullRequest, fileName: String) {
        val fileResponse = sendGithubGraphqlRequest(
            method = Method.POST,
            body = String.format(
                rawGithubFileQuery,
                *(pullRequest.repoFullName.split("/").toTypedArray()),
                "master",
                fileName
            ),
            accessToken = authorizationService.getAuthorizationToken(pullRequest.installationId)
        )


        val resultObject = fileResponse.obj("data")!!.obj("repository")!!

        if (resultObject["object"] == null)
            throw AnalysisException("$fileName is not found in ${pullRequest.branchName} branch, ${pullRequest.repoFullName} repo")

        val fileText = resultObject.obj("object")!!.string("text")!!

        solutionStorage.saveBase(pullRequest, fileName, fileText)
    }
}

private fun File.notExists(): Boolean {
    return !this.exists()
}
