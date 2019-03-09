package ru.nikstep.redink.git.webhook

import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import ru.nikstep.redink.checks.github.AnalysisStatusCheckService
import ru.nikstep.redink.git.GitException
import ru.nikstep.redink.git.inProgressStatus
import ru.nikstep.redink.model.PullRequestEvent
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.GitProperty.GITHUB
import ru.nikstep.redink.util.parseAsObject

/**
 * Implementation of the [AbstractWebhookService] for handling Github webhooks
 */
class GithubWebhookService(
    private val analysisStatusCheckService: AnalysisStatusCheckService,
    private val pullRequestRepository: PullRequestRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : AbstractWebhookService(pullRequestRepository, applicationEventPublisher) {

    private val logger = KotlinLogging.logger {}

    override val JsonObject.gitService: GitProperty
        get() = GITHUB

    override val JsonObject.repoId: Long?
        get() = -1

    override val JsonObject.number: Int?
        get() = int("number")

    override val JsonObject.repoFullName: String?
        get() = obj("repository")?.string("full_name")

    override val JsonObject.creatorName: String?
        get() = obj("pull_request")?.obj("user")?.string("login")

    override val JsonObject.headSha: String?
        get() = obj("pull_request")?.obj("head")?.string("sha")

    override val JsonObject.branchName: String?
        get() = obj("pull_request")?.obj("head")?.string("ref")

    override val JsonObject.secretKey: String?
        get() = obj("installation")?.int("id")?.toString()

    override val jsonToPullRequest: (JsonObject) -> PullRequest = { jsonPayload ->
        if (jsonPayload.hasInstallationId())
            super.jsonToPullRequest(jsonPayload)
                .apply(analysisStatusCheckService::sendInProgressStatus)
                .also(logger::inProgressStatus)
        else
            throw GitException("Git: no installation id")
    }

    private fun JsonObject.hasInstallationId(): Boolean {
        return obj("installation") != null
    }

    fun relaunch(payload: String) {
        val payloadObject = payload.parseAsObject()
        if (payloadObject["action"] == "rerequested") {
            val prNumber =
                (payloadObject.obj("check_run")?.array<Any>("pull_requests")?.get(0) as JsonObject).int("number")!!
            val repoFullName = payloadObject.obj("repository")?.string("full_name")!!
            val pullRequest =
                pullRequestRepository.findFirstByRepoFullNameAndNumberOrderByIdDesc(repoFullName, prNumber)
            applicationEventPublisher.publishEvent(PullRequestEvent(this, pullRequest))
        }
    }
}