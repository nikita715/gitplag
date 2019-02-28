package ru.nikstep.redink.github

import mu.KotlinLogging
import org.springframework.boot.configurationprocessor.json.JSONArray
import org.springframework.boot.configurationprocessor.json.JSONObject
import ru.nikstep.redink.checks.AnalysisResultData
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.checks.GithubAnalysisStatus
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.Git.GITHUB
import ru.nikstep.redink.util.JsonArrayDeserializer
import ru.nikstep.redink.util.RequestUtil.Companion.sendRestRequest
import ru.nikstep.redink.util.auth.AuthorizationService

class GithubPullRequestWebhookService(
    private val authorizationService: AuthorizationService,
    private val analysisStatusCheckService: AnalysisStatusCheckService,
    private val pullRequestRepository: PullRequestRepository
) : WebhookService {

    private val logger = KotlinLogging.logger {}

    @Synchronized
    override fun saveNewPullRequest(payload: String) {
        val jsonPayload = JSONObject(payload)
        if (jsonPayload.hasInstallationId()) {
            val pullRequest = fillPullRequestData(jsonPayload)
            logger.info {
                "Webhook: PullRequest: new from repo ${pullRequest.repoFullName}, user ${pullRequest.creatorName}," +
                        " branch ${pullRequest.branchName}, url https://github.com/${pullRequest.repoFullName}/pull/${pullRequest.number}"
            }
            pullRequestRepository.save(pullRequest)
            sendInProgressStatus(pullRequest)
        }
    }

    private fun fillPullRequestData(jsonPayload: JSONObject): PullRequest {

        val pullRequest = jsonPayload.getJSONObject("pull_request")

        val installationId = jsonPayload.getJSONObject("installation").getInt("id")
        val repoFullName = jsonPayload.getJSONObject("repository").getString("full_name")
        val prNumber = jsonPayload.getInt("number")

        val changeList = (sendRestRequest(
            url = "https://api.github.com/repos/$repoFullName/pulls/$prNumber/files",
            accessToken = authorizationService.getAuthorizationToken(installationId),
            deserializer = JsonArrayDeserializer()
        ) as JSONArray)

        val changedFilesList = (0 until changeList.length()).map { index ->
            (changeList.get(index) as JSONObject).getString("filename")
        }

        return PullRequest(
            gitService = GITHUB,
            number = jsonPayload.getInt("number"),
            installationId = installationId,
            creatorName = pullRequest.getJSONObject("user").getString("login"),
            repoId = -1,
            repoFullName = repoFullName,
            headSha = pullRequest.getJSONObject("head").getString("sha"),
            branchName = pullRequest.getJSONObject("head").getString("ref"),
            changedFiles = changedFilesList
        )
    }

    private fun sendInProgressStatus(pullRequest: PullRequest) {
        val analysisResultData =
            AnalysisResultData(status = GithubAnalysisStatus.IN_PROGRESS.value)
        analysisStatusCheckService.send(pullRequest, analysisResultData)
        logger.info {
            "Webhook: PullRequest: sent in progress status to repo ${pullRequest.repoFullName}, user ${pullRequest.creatorName}," +
                    " branch ${pullRequest.branchName}, url https://github.com/${pullRequest.repoFullName}/pull/${pullRequest.number}"
        }
    }
}

private fun JSONObject.hasInstallationId(): Boolean {
    return !this.isNull("installation")
}