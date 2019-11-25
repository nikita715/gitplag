package io.gitplag.git.payload

import com.beust.klaxon.JsonObject
import io.gitplag.git.agent.GithubAgent
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.BranchRepository
import io.gitplag.model.repo.PullRequestRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of the [AbstractGitManager] for handling Github webhooks
 */
class GithubManager(
    pullRequestRepository: PullRequestRepository,
    repositoryDataManager: RepositoryDataManager,
    githubLoader: GithubAgent,
    branchRepository: BranchRepository
) : AbstractGitManager(pullRequestRepository, repositoryDataManager, githubLoader, branchRepository) {

    override val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    override val git = GitProperty.GITHUB

    override val JsonObject.pullRequest: JsonObject
        get() = requireNotNull(obj("pull_request"))

    override val JsonObject?.number: Int?
        get() = this?.int("number")

    override val JsonObject?.creatorName: String?
        get() = this?.obj("head")?.obj("user")?.string("login")

    override val JsonObject?.sourceRepoId: String?
        get() = this?.obj("head")?.obj("repo")?.long("id")?.toString()

    override val JsonObject?.mainRepoId: String?
        get() = this?.obj("base")?.obj("repo")?.long("id")?.toString()

    override val JsonObject?.sourceRepoFullName: String?
        get() = this?.obj("head")?.obj("repo")?.string("full_name")

    override val JsonObject?.mainRepoFullName: String?
        get() = this?.obj("base")?.obj("repo")?.string("full_name")

    override val JsonObject?.sourceHeadSha: String?
        get() = this?.obj("head")?.string("sha")

    override val JsonObject?.sourceBranchName: String?
        get() = this?.obj("head")?.string("ref")

    override val JsonObject?.mainBranchName: String?
        get() = this?.obj("base")?.string("ref")

    override val JsonObject?.createdAt: LocalDateTime?
        get() = this?.string("created_at")?.parseDate()

    override val JsonObject?.updatedAt: LocalDateTime?
        get() = this?.string("updated_at")?.parseDate()

    override val JsonObject.pushBranchName: String?
        get() = string("ref")?.substringAfterLast("/")

    override val JsonObject.pushLastUpdated: LocalDateTime?
        get() = obj("repository")?.string("updated_at").parseDate()

    override val JsonObject.pushRepoName: String?
        get() = obj("repository")?.string("full_name")

    override val JsonObject.pushRepoId: String?
        get() = obj("repository")?.long("id")?.toString()

    override val JsonObject.branchUpdatedAt: LocalDateTime?
        get() = obj("commit")?.obj("commit")?.obj("author")?.string("date").parseDate()
}