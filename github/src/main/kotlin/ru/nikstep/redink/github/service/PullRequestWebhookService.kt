package ru.nikstep.redink.github.service

import mu.KotlinLogging
import org.springframework.boot.configurationprocessor.json.JSONArray
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.http.HttpMethod
import ru.nikstep.redink.analysis.AnalysisService
import ru.nikstep.redink.data.AnalysisResultData
import ru.nikstep.redink.data.GithubAnalysisStatus
import ru.nikstep.redink.data.PullRequestData
import ru.nikstep.redink.github.util.JsonArrayDeserializer
import ru.nikstep.redink.github.util.RequestUtil
import ru.nikstep.redink.model.repo.RepositoryRepository
import java.lang.String.format

class PullRequestWebhookService(
    private val repositoryRepository: RepositoryRepository,
    private val sourceCodeService: SourceCodeService,
    private val githubAppService: GithubAppService,
    private val plagiarismService: PlagiarismService,
    private val analysisService: AnalysisService,
    private val analysisResultService: AnalysisResultService
) {

    private val rawGithubFileQuery = "{\"query\": \"query {repository(name: \\\"%s\\\", owner: \\\"%s\\\")" +
            " {object(expression: \\\"%s:%s\\\") {... on Blob{text}}}}\"}"

    private val logger = KotlinLogging.logger {}

    @Synchronized
    fun processPullRequest(payload: String) {
        val data = fillPullRequestData(payload)
        logger.info {
            "PullRequest: new from repo ${data.repoFullName}, user ${data.creatorName}," +
                    " branch ${data.branchName}, url https://github.com/${data.repoFullName}/pull/${data.number}"
        }
        sendInProgressStatus(data)
        loadFiles(data)
        analysisService.analyse(data)
        plagiarismService.analyze(data)
    }

    private fun fillPullRequestData(payload: String): PullRequestData {
        val jsonPayload = JSONObject(payload)

        val pullRequest = jsonPayload.getJSONObject("pull_request")

        val installationId = jsonPayload.getJSONObject("installation").getInt("id")
        val repoFullName = jsonPayload.getJSONObject("repository").getString("full_name")
        val prNumber = jsonPayload.getInt("number")

        val changeList = (RequestUtil.sendRestRequest(
            url = "https://api.github.com/repos/$repoFullName/pulls/$prNumber/files",
            accessToken = githubAppService.getAccessToken(installationId),
            deserializer = JsonArrayDeserializer()
        ) as JSONArray)


        val changedFilesList = mutableListOf<String>()
        for (index in 0 until changeList.length()) {
            changedFilesList.add((changeList.get(index) as JSONObject).getString("filename"))
        }

        val data = PullRequestData(
            number = jsonPayload.getInt("number"),
            installationId = installationId,
            creatorName = pullRequest.getJSONObject("user").getString("login"),
            repoOwnerName = jsonPayload.getJSONObject("repository")
                .getJSONObject("owner").getString("login"),
            repoName = jsonPayload.getJSONObject("repository").getString("name"),
            repoFullName = repoFullName,
            headSha = pullRequest.getJSONObject("head").getString("sha"),
            branchName = pullRequest.getJSONObject("head").getString("ref"),
            changedFiles = changedFilesList
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

            val resultObject = fileResponse.getJSONObject("data").getJSONObject("repository")

            if (resultObject.isNull("object")) {
                logger.error { "$fileName is not found in ${data.branchName} branch, ${data.repoFullName} repo" }
            } else {
                sourceCodeService.save(
                    data, fileName,
                    resultObject.getJSONObject("object").getString("text")
                )
            }
        }
    }

    private fun sendInProgressStatus(prData: PullRequestData) {
        val analysisResultData =
            AnalysisResultData(status = GithubAnalysisStatus.IN_PROGRESS.value)
        analysisResultService.send(prData, analysisResultData)
    }

}