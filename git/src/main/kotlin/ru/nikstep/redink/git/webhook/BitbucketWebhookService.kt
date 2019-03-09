package ru.nikstep.redink.git.webhook

import com.beust.klaxon.JsonObject
import org.springframework.context.ApplicationEventPublisher
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.GitProperty.BITBUCKET
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of the [AbstractWebhookService] for handling Bitbucket webhooks
 */
class BitbucketWebhookService(
    pullRequestRepository: PullRequestRepository,
    applicationEventPublisher: ApplicationEventPublisher
) : AbstractWebhookService(pullRequestRepository, applicationEventPublisher) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    override val JsonObject.gitService: GitProperty
        get() = BITBUCKET

    override val JsonObject.repoId: Long?
        get() = -1

    override val JsonObject.number: Int?
        get() = obj("pullrequest")?.int("id")

    override val JsonObject.repoFullName: String?
        get() = obj("pullrequest")?.obj("destination")?.obj("repository")?.string("full_name")

    override val JsonObject.creatorName: String?
        get() = obj("pullrequest")?.obj("author")?.string("username")

    override val JsonObject.headSha: String?
        get() = obj("pullrequest")?.obj("source")?.obj("commit")?.string("hash")

    override val JsonObject.branchName: String?
        get() = obj("pullrequest")?.obj("source")?.obj("branch")?.string("name")

    override val JsonObject.date: LocalDateTime?
        get() = LocalDateTime.parse(
            obj("pullrequest")?.string("updated_on")?.substring(0, 19),
            dateFormatter
        )
}
