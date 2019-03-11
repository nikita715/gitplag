package ru.nikstep.redink.git.webhook

import com.beust.klaxon.JsonObject
import org.springframework.context.ApplicationEventPublisher
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.GitProperty.GITLAB
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of the [AbstractWebhookService] for handling Gitlab webhooks
 */
class GitlabWebhookService(
    pullRequestRepository: PullRequestRepository,
    applicationEventPublisher: ApplicationEventPublisher
) : AbstractWebhookService(pullRequestRepository, applicationEventPublisher) {

    override val JsonObject.mainHeadSha: String?
        get() = TODO("not implemented")
    override val JsonObject.sourceRepoFullName: String?
        get() = TODO("not implemented")
    override val JsonObject.mainBranchName: String?
        get() = TODO("not implemented")
    override val JsonObject.mainRepoId: Long?
        get() = TODO("not implemented")

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    override val JsonObject.gitService: GitProperty
        get() = GITLAB

    override val JsonObject.sourceRepoId: Long?
        get() = obj("project")?.long("id")

    override val JsonObject.number: Int?
        get() = obj("object_attributes")?.int("iid")

    override val JsonObject.mainRepoFullName: String?
        get() = obj("project")?.string("path_with_namespace")

    override val JsonObject.creatorName: String?
        get() = obj("user")?.string("username")

    override val JsonObject.sourceHeadSha: String?
        get() = obj("object_attributes")?.obj("last_commit")?.string("id")

    override val JsonObject.sourceBranchName: String?
        get() = obj("object_attributes")?.string("source_branch")

    override val JsonObject.date: LocalDateTime?
        get() = LocalDateTime.parse(
            obj("object_attributes")?.string("last_edited_at")?.substring(0, 19),
            dateFormatter
        )
}