package ru.nikstep.redink.git.webhook

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.git.loader.BitbucketLoader
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.GitProperty.BITBUCKET
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of the [AbstractPayloadProcessor] for handling Bitbucket webhooks
 */
class BitbucketPayloadProcessor(
    pullRequestRepository: PullRequestRepository,
    repositoryRepository: RepositoryRepository,
    bitbucketLoader: BitbucketLoader
) : AbstractPayloadProcessor(pullRequestRepository, repositoryRepository, bitbucketLoader) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    override val git = BITBUCKET

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

    override val JsonObject?.date: LocalDateTime?
        get() = LocalDateTime.parse(
            this?.string("updated_on")?.substring(0, 19),
            dateFormatter
        )

    override val JsonObject.pushRepoId: Long?
        get() = -1

    override val JsonObject.pushRepoName: String?
        get() = obj("repository")?.string("full_name")

    override val JsonObject.pushBranchName: String?
        get() = obj("push")?.array<JsonObject>("changes")?.get(0)?.obj("new")?.string("name")
}
