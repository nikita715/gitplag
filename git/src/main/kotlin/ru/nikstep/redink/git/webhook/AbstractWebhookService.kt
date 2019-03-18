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
                number = requireNotNull(number),
                creatorName = requireNotNull(creatorName),
                sourceRepoId = requireNotNull(sourceRepoId),
                mainRepoId = requireNotNull(mainRepoId),
                sourceRepoFullName = requireNotNull(sourceRepoFullName),
                mainRepoFullName = requireNotNull(mainRepoFullName),
                headSha = requireNotNull(sourceHeadSha),
                sourceBranchName = requireNotNull(sourceBranchName),
                mainBranchName = requireNotNull(mainBranchName),
                date = requireNotNull(date)
            )
        }
    }

    protected abstract val JsonObject.gitService: GitProperty

    protected abstract val JsonObject.sourceRepoId: Long?

    protected abstract val JsonObject.number: Int?

    protected abstract val JsonObject.mainRepoFullName: String?

    protected abstract val JsonObject.creatorName: String?

    protected abstract val JsonObject.sourceHeadSha: String?

    protected abstract val JsonObject.sourceBranchName: String?

    protected abstract val JsonObject.date: LocalDateTime?

    protected abstract val JsonObject.sourceRepoFullName: String?

    protected abstract val JsonObject.mainBranchName: String?

    abstract val JsonObject.mainRepoId: Long?
}