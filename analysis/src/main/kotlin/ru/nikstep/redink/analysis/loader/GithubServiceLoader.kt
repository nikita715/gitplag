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

    private val rawGithubFileQuery = "{\"query\": \"query {repository(name: \\\"%s\\\", owner: \\\"%s\\\")" +
            " {object(expression: \\\"%s:%s\\\") {... on Blob{text}}}}\"}"

    override fun loadFilesFromGit(data: PullRequest) {


        val fileNames = data.changedFiles.intersect(repositoryRepository.findByName(data.repoFullName).filePatterns)

        fileNames.map { fileName ->
            checkBaseExists(data, fileName)

            val fileResponse = sendGraphqlRequest(
                httpMethod = HttpMethod.POST,
                body = String.format(
                    rawGithubFileQuery,
                    data.repoName,
                    data.repoOwnerName,
                    data.branchName,
                    fileName
                ),
                accessToken = authorizationService.getAuthorizationToken(data.installationId)
            )

            val resultObject = fileResponse.getJSONObject("data").getJSONObject("repository")

            if (resultObject.isNull("object"))
                throw AnalysisException("$fileName is not found in ${data.branchName} branch, ${data.repoFullName} repo")

            solutionStorageService.saveSolution(
                data, fileName,
                resultObject.getJSONObject("object").getString("text")
            )
        }
    }

    private fun checkBaseExists(data: PullRequest, fileName: String) {
        val base = solutionStorageService.loadBase(data.repoFullName, fileName)
        if (base.notExists()) saveBase(data, fileName)
    }

    private fun saveBase(data: PullRequest, fileName: String) {
        val fileResponse = sendGraphqlRequest(
            httpMethod = HttpMethod.POST,
            body = String.format(
                rawGithubFileQuery,
                data.repoName,
                data.repoOwnerName,
                "master",
                fileName
            ),
            accessToken = authorizationService.getAuthorizationToken(data.installationId)
        )


        val resultObject = fileResponse.getJSONObject("data").getJSONObject("repository")

        if (resultObject.isNull("object"))
            throw AnalysisException("$fileName is not found in ${data.branchName} branch, ${data.repoFullName} repo")

        val fileText = resultObject.getJSONObject("object").getString("text")

        solutionStorageService.saveBase(data, fileName, fileText)
    }
}

private fun File.notExists(): Boolean {
    return !this.exists()
}
