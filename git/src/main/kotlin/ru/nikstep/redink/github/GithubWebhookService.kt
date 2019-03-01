package ru.nikstep.redink.github

import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.checks.AnalysisResultData
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.checks.GithubAnalysisStatus
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty
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

    override val JsonObject.gitService: GitProperty
        get() = GITHUB

    override val JsonObject.repoId: Long
        get() = -1

    override val JsonObject.number: Int
        get() = int("number")!!

    override val JsonObject.repoFullName: String
        get() = obj("repository")!!.string("full_name")!!

    override val JsonObject.creatorName: String
        get() = obj("pull_request")!!.obj("user")!!.string("login")!!

    override val JsonObject.headSha: String
        get() = obj("pull_request")!!.obj("head")!!.string("sha")!!

    override val JsonObject.branchName: String
        get() = obj("pull_request")!!.obj("head")!!.string("ref")!!

    override val JsonObject.installationId: Int
        get() = obj("installation")!!.int("id")!!

    override val JsonObject.changedFiles: List<String>
        get() = sendRestRequest(
            url = "https://api.github.com/repos/$repoFullName/pulls/$number/files",
            accessToken = authorizationService.getAuthorizationToken(installationId),
            deserializer = JsonArrayDeserializer
        ).map { (it as JsonObject).string("filename")!! }

    override val jsonToPullRequest: (JsonObject) -> PullRequest = { jsonPayload ->
        if (jsonPayload.hasInstallationId())
            super.jsonToPullRequest(jsonPayload)
                .apply(::sendInProgressStatus)
                .also(logger::inProgressStatus)
        else
            throw GitException("Git: no installation id")
    }

    private fun sendInProgressStatus(pullRequest: PullRequest) {
        AnalysisResultData(status = GithubAnalysisStatus.IN_PROGRESS.value).also {
            analysisStatusCheckService.send(pullRequest, it)
        }
    }

    private fun JsonObject.hasInstallationId(): Boolean {
        return this["installation"] == null
    }
}