package ru.nikstep.redink.github

import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.checks.AnalysisResultData
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.checks.GithubAnalysisStatus
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty.GITHUB
import ru.nikstep.redink.util.JsonArrayDeserializer
import ru.nikstep.redink.util.auth.AuthorizationService
import ru.nikstep.redink.util.sendRestRequest

class GithubPullRequestWebhookService(
    private val authorizationService: AuthorizationService,
    private val analysisStatusCheckService: AnalysisStatusCheckService,
    pullRequestRepository: PullRequestRepository
) : AbstractWebhookService(pullRequestRepository) {

    private val logger = KotlinLogging.logger {}

    override val jsonToPullRequest: (JsonObject) -> PullRequest = { jsonPayload ->
        if (jsonPayload.hasInstallationId()) {
            jsonPayload.toPullRequest().apply(::sendInProgressStatus)
        } else {
            throw GitException("Git: no installation id")
        }
    }

    private fun JsonObject.toPullRequest(): PullRequest {

        val pullRequest = this.obj("pull_request")!!

        val installationId = this.obj("installation")!!.int("id")!!
        val repoFullName = this.obj("repository")!!.string("full_name")!!
        val prNumber = this.int("number")!!

        val changeList = sendRestRequest(
            url = "https://api.github.com/repos/$repoFullName/pulls/$prNumber/files",
            accessToken = authorizationService.getAuthorizationToken(installationId),
            deserializer = JsonArrayDeserializer
        )

        return PullRequest(
            gitService = GITHUB,
            number = this.int("number")!!,
            installationId = installationId,
            creatorName = pullRequest.obj("user")!!.string("login")!!,
            repoId = -1,
            repoFullName = repoFullName,
            headSha = pullRequest.obj("head")!!.string("sha")!!,
            branchName = pullRequest.obj("head")!!.string("ref")!!,
            changedFiles = changeList.map { (it as JsonObject).string("filename")!! }
        )
    }

    private fun sendInProgressStatus(pullRequest: PullRequest) {
        AnalysisResultData(status = GithubAnalysisStatus.IN_PROGRESS.value).also {
            analysisStatusCheckService.send(pullRequest, it)
        }
        logger.inProgressStatus(pullRequest)
    }

    private fun JsonObject.hasInstallationId(): Boolean {
        return this["installation"] == null
    }
}