package ru.nikstep.redink.git.webhook

import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.git.loader.GithubLoader
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.GitProperty.GITHUB
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of the [AbstractPayloadProcessor] for handling Github webhooks
 */
class GithubPayloadProcessor(
    pullRequestRepository: PullRequestRepository,
    repositoryRepository: RepositoryRepository,
    githubLoader: GithubLoader
) : AbstractPayloadProcessor(pullRequestRepository, repositoryRepository, githubLoader) {

    private val logger = KotlinLogging.logger {}
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    override val git = GITHUB

    override val JsonObject.pullRequest: JsonObject
        get() = requireNotNull(obj("pull_request"))

    override val JsonObject?.number: Int?
        get() = this?.int("number")

    override val JsonObject?.creatorName: String?
        get() = this?.obj("head")?.obj("user")?.string("login")

    override val JsonObject?.sourceRepoId: Long?
        get() = this?.obj("head")?.obj("repo")?.long("id")

    override val JsonObject?.mainRepoId: Long?
        get() = this?.obj("base")?.obj("repo")?.long("id")

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

    override val JsonObject?.date: LocalDateTime?
        get() = LocalDateTime.parse(
            this?.string("updated_at")?.substring(0, 19),
            dateFormatter
        )

    override val JsonObject.pushBranchName: String?
        get() = string("ref")?.substringAfterLast("/")

    override val JsonObject.pushRepoName: String?
        get() = obj("repository")?.string("full_name")

    override val JsonObject.pushRepoId: Long?
        get() = -1
}