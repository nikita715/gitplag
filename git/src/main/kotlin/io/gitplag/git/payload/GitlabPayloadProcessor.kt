package io.gitplag.git.payload

import com.beust.klaxon.JsonObject
import io.gitplag.git.rest.GitlabRestManager
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.PullRequestRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of the [AbstractPayloadProcessor] for handling Gitlab webhooks
 */
class GitlabPayloadProcessor(
    pullRequestRepository: PullRequestRepository,
    repositoryDataManager: RepositoryDataManager,
    gitlabLoader: GitlabRestManager
) : AbstractPayloadProcessor(pullRequestRepository, repositoryDataManager, gitlabLoader) {

    override val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    override val git = GitProperty.GITLAB

    override val JsonObject.pullRequest: JsonObject
        get() = requireNotNull(obj("object_attributes"))

    override val JsonObject?.number: Int?
        get() = this?.int("iid")

    override val JsonObject?.creatorName: String?
        get() = this?.obj("source")?.string("namespace")

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

    override val JsonObject?.createdAt: LocalDateTime?
        get() = this?.string("created_at")?.parseDate()

    override val JsonObject?.updatedAt: LocalDateTime?
        get() = this?.string("last_edited_at")?.parseDate()

    override val JsonObject.pushRepoName: String?
        get() = obj("project")?.string("path_with_namespace")

    override val JsonObject.pushBranchName: String?
        get() = string("ref")?.substringAfter("refs/heads/")

    override val JsonObject.pushRepoId: Long?
        get() = long("project_id")
}