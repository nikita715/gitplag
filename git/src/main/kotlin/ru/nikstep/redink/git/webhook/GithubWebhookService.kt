package ru.nikstep.redink.git.webhook

import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import ru.nikstep.redink.checks.github.AnalysisStatusCheckService
import ru.nikstep.redink.git.GitException
import ru.nikstep.redink.git.PullRequestEvent
import ru.nikstep.redink.git.inProgressStatus
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.GitProperty.GITHUB
import ru.nikstep.redink.util.parseAsObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of the [AbstractWebhookService] for handling Github webhooks
 */
class GithubWebhookService(
    private val analysisStatusCheckService: AnalysisStatusCheckService,
    private val pullRequestRepository: PullRequestRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : AbstractWebhookService(pullRequestRepository, applicationEventPublisher) {

    private val logger = KotlinLogging.logger {}
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    override val JsonObject.gitService: GitProperty
        get() = GITHUB

    override val JsonObject.number: Int?
        get() = int("number")

    override val JsonObject.creatorName: String?
        get() = obj("pull_request")?.obj("head")?.obj("user")?.string("login")

    override val JsonObject.sourceRepoId: Long?
        get() = obj("pull_request")?.obj("head")?.obj("repo")?.long("id")

    override val JsonObject.mainRepoId: Long?
        get() = obj("pull_request")?.obj("base")?.obj("repo")?.long("id")

    override val JsonObject.sourceRepoFullName: String?
        get() = obj("pull_request")?.obj("head")?.obj("repo")?.string("full_name")

    override val JsonObject.mainRepoFullName: String?
        get() = obj("pull_request")?.obj("base")?.obj("repo")?.string("full_name")

    override val JsonObject.sourceHeadSha: String?
        get() = obj("pull_request")?.obj("head")?.string("sha")

    override val JsonObject.sourceBranchName: String?
        get() = obj("pull_request")?.obj("head")?.string("ref")

    override val JsonObject.mainBranchName: String?
        get() = obj("pull_request")?.obj("base")?.string("ref")

    override val JsonObject.secretKey: String?
        get() = obj("installation")?.int("id")?.toString()

    override val JsonObject.date: LocalDateTime?
        get() = LocalDateTime.parse(
            obj("pull_request")?.string("updated_at")?.substring(0, 19),
            dateFormatter
        )

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
            val prNumber = requireNotNull(
                (
                        payloadObject.obj("check_run")?.array<Any>("pull_requests")?.get(0) as JsonObject).int("number")
            )
            val repoFullName = requireNotNull(payloadObject.obj("repository")?.string("full_name"))
            val pullRequest =
                pullRequestRepository.findFirstBySourceRepoFullNameAndNumberOrderByIdDesc(repoFullName, prNumber)
            applicationEventPublisher.publishEvent(PullRequestEvent(this, pullRequest))
        }
    }
}