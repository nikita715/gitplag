package ru.nikstep.redink.service

import mu.KotlinLogging
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.HttpMethod
import ru.nikstep.redink.data.PullRequestData
import ru.nikstep.redink.repo.RepositoryRepository
import ru.nikstep.redink.util.RequestUtil
import java.lang.String.format

class PullRequestWebhookService(
    private val repositoryRepository: RepositoryRepository,
    private val sourceCodeService: SourceCodeService,
    private val githubAppService: GithubAppService,
    private val plagiarismService: PlagiarismService
) {

    private val rawGithubFileQuery = "{\"query\": \"query {repository(name: \\\"%s\\\", owner: \\\"%s\\\")" +
            " {object(expression: \\\"%s:%s\\\") {... on Blob{text}}}}\"}"
    private val githubPullRequestLink = "https://github.com/%s/pull/%s"

    private val logger = KotlinLogging.logger {}

    @Synchronized
    fun processPullRequest(payload: String) {
        val data = fillPullRequestData(payload)
        logger.info {
            "PullRequest: new from repo ${data.repoFullName}, user ${data.creatorName}," +
                    " branch ${data.branchName}, url https://github.com/${data.repoFullName}/pull/${data.number}"
        }
        loadFiles(data)
        plagiarismService.analyze(data)
    }

    private fun fillPullRequestData(payload: String): PullRequestData {
        val jsonPayload = JSONObject(payload)

        val pullRequest = jsonPayload.getJSONObject("pull_request")

        val data = PullRequestData(
            number = jsonPayload.getInt("number"),
            installationId = jsonPayload.getJSONObject("installation").getInt("id"),
            creatorName = pullRequest.getJSONObject("user").getString("login"),
            repoOwnerName = jsonPayload.getJSONObject("repository")
                .getJSONObject("owner").getString("login"),
            repoName = jsonPayload.getJSONObject("repository").getString("name"),
            repoFullName = jsonPayload.getJSONObject("repository").getString("full_name"),
            headSha = pullRequest.getJSONObject("head").getString("sha"),
            branchName = pullRequest.getJSONObject("head").getString("ref")
        )

        return data
    }

    private fun loadFiles(data: PullRequestData) {
        val fileNames = repositoryRepository.findByName(data.repoFullName).filePatterns

        for (fileName in fileNames) {
            val fileResponse = RequestUtil.sendGraphqlRequest(
                httpMethod = HttpMethod.POST,
                body = format(rawGithubFileQuery, data.repoName, data.repoOwnerName, data.branchName, fileName),
                accessToken = githubAppService.getAccessToken(data.installationId)
            )
            val fileData = fileResponse.getJSONObject("data").getJSONObject("repository")
                .getJSONObject("object").getString("text")
            sourceCodeService.save(data, fileName, fileData)
        }
    }

}