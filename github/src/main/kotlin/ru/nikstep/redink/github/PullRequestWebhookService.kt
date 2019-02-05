package ru.nikstep.redink.github

import mu.KotlinLogging
import org.springframework.boot.configurationprocessor.json.JSONArray
import org.springframework.boot.configurationprocessor.json.JSONObject
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.data.AnalysisResultData
import ru.nikstep.redink.data.GithubAnalysisStatus
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.JsonArrayDeserializer
import ru.nikstep.redink.util.RequestUtil
import ru.nikstep.redink.util.auth.AuthorizationService

class PullRequestWebhookService(
    private val authorizationService: AuthorizationService,
    private val analysisStatusCheckService: AnalysisStatusCheckService,
    private val pullRequestRepository: PullRequestRepository
) {

    private val logger = KotlinLogging.logger {}

    @Synchronized
    fun processPullRequest(payload: String) {
        val data = fillPullRequestData(payload)
        logger.info {
            "Webhook: PullRequest: new from repo ${data.repoFullName}, user ${data.creatorName}," +
                    " branch ${data.branchName}, url https://github.com/${data.repoFullName}/pull/${data.number}"
        }
        pullRequestRepository.save(data)
        sendInProgressStatus(data)
    }

    private fun fillPullRequestData(payload: String): PullRequest {
        val jsonPayload = JSONObject(payload)

        val pullRequest = jsonPayload.getJSONObject("pull_request")

        val installationId = jsonPayload.getJSONObject("installation").getInt("id")
        val repoFullName = jsonPayload.getJSONObject("repository").getString("full_name")
        val prNumber = jsonPayload.getInt("number")

        val changeList = (RequestUtil.sendRestRequest(
            url = "https://api.github.com/repos/$repoFullName/pulls/$prNumber/files",
            accessToken = authorizationService.getAuthorizationToken(installationId),
            deserializer = JsonArrayDeserializer()
        ) as JSONArray)

        val changedFilesList = mutableListOf<String>()
        for (index in 0 until changeList.length()) {
            changedFilesList.add((changeList.get(index) as JSONObject).getString("filename"))
        }

        val data = PullRequest(
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

    private fun sendInProgressStatus(prData: PullRequest) {
        val analysisResultData =
            AnalysisResultData(status = GithubAnalysisStatus.IN_PROGRESS.value)
        analysisStatusCheckService.send(prData, analysisResultData)
        logger.info {
            "Webhook: PullRequest: sent in progress status to repo ${prData.repoFullName}, user ${prData.creatorName}," +
                    " branch ${prData.branchName}, url https://github.com/${prData.repoFullName}/pull/${prData.number}"
        }
    }
}