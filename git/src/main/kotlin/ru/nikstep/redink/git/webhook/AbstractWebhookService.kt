package ru.nikstep.redink.git.webhook

import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import ru.nikstep.redink.git.newPullRequest
import ru.nikstep.redink.model.PullRequestEvent
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.parseAsObject
import java.time.LocalDateTime

/**
 * Common implementation of the [WebhookService]
 */
abstract class AbstractWebhookService(
    private val pullRequestRepository: PullRequestRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) : WebhookService {
    private val logger = KotlinLogging.logger {}

    override fun saveNewPullRequest(payload: String) {
        payload.parseAsObject()
            .let(jsonToPullRequest)
            .let(pullRequestRepository::save)
            .apply(logger::newPullRequest)
            .also { applicationEventPublisher.publishEvent(PullRequestEvent(this, it)) }
    }

    open val jsonToPullRequest: (JsonObject) -> PullRequest = { jsonObject ->
        jsonObject.run {
            PullRequest(
                gitService = gitService,
                repoId = requireNotNull(repoId) { "repo id is null" },
                number = requireNotNull(number) { "number is null" },
                repoFullName = requireNotNull(repoFullName) { "repoFullName is null" },
                creatorName = requireNotNull(creatorName) { "creatorName is null" },
                headSha = requireNotNull(headSha) { "headSha is null" },
                branchName = requireNotNull(branchName) { "branchName is null" },
                secretKey = requireNotNull(secretKey) { "secretKey is null" },
                date = requireNotNull(date) { "date is null" }
            )
        }
    }

    protected abstract val JsonObject.gitService: GitProperty

    protected abstract val JsonObject.repoId: Long?

    protected abstract val JsonObject.number: Int?

    protected abstract val JsonObject.repoFullName: String?

    protected abstract val JsonObject.creatorName: String?

    protected abstract val JsonObject.headSha: String?

    protected abstract val JsonObject.branchName: String?

    protected abstract val JsonObject.date: LocalDateTime?

    protected open val JsonObject.secretKey: String?
        get() = ""
}