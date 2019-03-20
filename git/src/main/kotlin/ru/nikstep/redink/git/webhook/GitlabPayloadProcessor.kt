package ru.nikstep.redink.git.webhook

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.git.loader.GitlabLoader
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.GitProperty.GITLAB
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of the [AbstractPayloadProcessor] for handling Gitlab webhooks
 */
class GitlabPayloadProcessor(
    pullRequestRepository: PullRequestRepository,
    repositoryRepository: RepositoryRepository,
    gitlabLoader: GitlabLoader
) : AbstractPayloadProcessor(pullRequestRepository, repositoryRepository, gitlabLoader) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    override val git = GITLAB

    override val JsonObject.pullRequest: JsonObject
        get() = requireNotNull(obj("object_attributes"))

    override val JsonObject?.number: Int?
        get() = this?.int("iid")

    override val JsonObject?.creatorName: String?
        get() = this?.obj("author")?.string("username")

    override val JsonObject?.sourceRepoId: Long?
        get() = this?.long("source_project_id")

    override val JsonObject?.mainRepoId: Long?
        get() = this?.long("target_project_id")

    override val JsonObject?.sourceRepoFullName: String?
        get() = this?.obj("source")?.string("path_with_namespace")

    override val JsonObject?.mainRepoFullName: String?
        get() = this?.obj("target")?.string("path_with_namespace")

    override val JsonObject?.sourceHeadSha: String?
        get() = this?.obj("last_commit")?.string("id")

    override val JsonObject?.sourceBranchName: String?
        get() = this?.string("source_branch")

    override val JsonObject?.mainBranchName: String?
        get() = this?.string("target_branch")

    override val JsonObject?.date: LocalDateTime?
        get() = LocalDateTime.parse(
            this?.string("last_edited_at")?.substring(0, 19),
            dateFormatter
        )

    override val JsonObject.pushRepoName: String?
        get() = obj("project")?.string("path_with_namespace")

    override val JsonObject.pushBranchName: String?
        get() = string("ref")?.substringAfter("refs/heads/")

    override val JsonObject.pushRepoId: Long?
        get() = long("project_id")
}