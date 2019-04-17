package io.gitplag.git.payload

import com.beust.klaxon.JsonObject
import io.gitplag.git.rest.BitbucketRestManager
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.PullRequestRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of the [AbstractPayloadProcessor] for handling Bitbucket webhooks
 */
class BitbucketPayloadProcessor(
    pullRequestRepository: PullRequestRepository,
    repositoryDataManager: RepositoryDataManager,
    bitbucketLoader: BitbucketRestManager
) : AbstractPayloadProcessor(pullRequestRepository, repositoryDataManager, bitbucketLoader) {

    override val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    override val git = GitProperty.BITBUCKET

    override val JsonObject.pullRequest: JsonObject
        get() = requireNotNull(obj("pullrequest"))

    override val JsonObject?.number: Int?
        get() = this?.int("id")

    override val JsonObject?.creatorName: String?
        get() = this?.obj("author")?.string("username")

    override val JsonObject?.sourceRepoId: Long?
        get() = -1

    override val JsonObject?.mainRepoId: Long?
        get() = -1

    override val JsonObject?.sourceRepoFullName: String?
        get() = this?.obj("source")?.obj("repository")?.string("full_name")

    override val JsonObject?.mainRepoFullName: String?
        get() = this?.obj("destination")?.obj("repository")?.string("full_name")

    override val JsonObject?.sourceHeadSha: String?
        get() = this?.obj("source")?.obj("commit")?.string("hash")

    override val JsonObject?.sourceBranchName: String?
        get() = this?.obj("source")?.obj("branch")?.string("name")

    override val JsonObject?.mainBranchName: String?
        get() = this?.obj("destination")?.obj("branch")?.string("name")

    override val JsonObject?.createdAt: LocalDateTime?
        get() = this?.string("created_on")?.parseDate()

    override val JsonObject?.updatedAt: LocalDateTime?
        get() = this?.string("updated_on")?.parseDate()

    override val JsonObject.pushRepoId: Long?
        get() = -1

    override val JsonObject.pushRepoName: String?
        get() = obj("repository")?.string("full_name")

    override val JsonObject.pushBranchName: String?
        get() = obj("push")?.array<JsonObject>("changes")?.get(0)?.obj("new")?.string("name")
}
