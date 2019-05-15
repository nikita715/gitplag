package io.gitplag.git.payload

import com.beust.klaxon.JsonObject
import io.gitplag.git.rest.GitlabRestManager
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.BranchRepository
import io.gitplag.model.repo.PullRequestRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of the [AbstractPayloadProcessor] for handling Gitlab webhooks
 */
class GitlabPayloadProcessor(
    pullRequestRepository: PullRequestRepository,
    repositoryDataManager: RepositoryDataManager,
    private val gitlabLoader: GitlabRestManager,
    branchRepository: BranchRepository
) : AbstractPayloadProcessor(pullRequestRepository, repositoryDataManager, gitlabLoader, branchRepository) {

    override val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    override val git = GitProperty.GITLAB

    override val JsonObject.pullRequest: JsonObject
        get() = requireNotNull(obj("object_attributes"))

    override val JsonObject?.number: Int?
        get() = this?.int("iid")

    override val JsonObject?.creatorName: String?
        get() = this?.obj("source")?.string("namespace") ?: this?.obj("author")?.string("username")

    override val JsonObject?.sourceRepoId: String?
        get() = this?.long("source_project_id")?.toString()

    override val JsonObject?.mainRepoId: String?
        get() = this?.long("target_project_id")?.toString()

    override val JsonObject?.sourceRepoFullName: String?
        get() = this?.obj("source")?.string("path_with_namespace") ?: gitlabLoader.repoNameById(this.sourceRepoId)

    override val JsonObject?.mainRepoFullName: String?
        get() = this?.obj("target")?.string("path_with_namespace") ?: gitlabLoader.repoNameById(this.mainRepoId)

    override val JsonObject?.sourceHeadSha: String?
        get() = this?.obj("last_commit")?.string("id") ?: this?.string("sha")

    override val JsonObject?.sourceBranchName: String?
        get() = this?.string("source_branch")

    override val JsonObject?.mainBranchName: String?
        get() = this?.string("target_branch")

    override val JsonObject?.createdAt: LocalDateTime?
        get() = this?.string("created_at")?.parseDate()

    override val JsonObject?.updatedAt: LocalDateTime?
        get() = (this?.string("last_edited_at") ?: this?.string("updated_at"))?.parseDate()

    override val JsonObject.pushRepoName: String?
        get() = obj("project")?.string("path_with_namespace")

    override val JsonObject.pushBranchName: String?
        get() = string("ref")?.substringAfter("refs/heads/")

    override val JsonObject.pushRepoId: String?
        get() = long("project_id")?.toString()

    override val JsonObject.pushLastUpdated: LocalDateTime?
        get() = array<JsonObject>("commits")?.last()?.string("timestamp")?.parseDate()

    override val JsonObject.branchUpdatedAt: LocalDateTime?
        get() = obj("commit")?.string("committed_date").parseDate()

    override fun prFromTheSameRepo(json: JsonObject) =
        json.run { long("source_project_id") == long("target_project_id") }
}