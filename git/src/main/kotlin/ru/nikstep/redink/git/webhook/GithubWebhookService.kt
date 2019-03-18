package ru.nikstep.redink.git.webhook

import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.git.loader.GithubLoader
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.GitProperty.GITHUB
import ru.nikstep.redink.util.parseAsObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of the [AbstractWebhookService] for handling Github webhooks
 */
class GithubWebhookService(
    pullRequestRepository: PullRequestRepository,
    private val githubLoader: GithubLoader
) : AbstractWebhookService(pullRequestRepository) {

    override fun saveNewBaseFiles(payload: String) {
        val jsonObject = payload.parseAsObject()
        val branchName = requireNotNull(jsonObject.string("ref")?.substringAfterLast("/"))
        val repoFullName = requireNotNull(jsonObject.obj("repository")?.string("full_name"))
        val added = requireNotNull(jsonObject.obj("head_commit")?.array<String>("added"))
        val modified = requireNotNull(jsonObject.obj("head_commit")?.array<String>("modified"))
        added.forEach { fileName ->
            githubLoader.loadBase(repoFullName, branchName, fileName)
        }
        modified.forEach { fileName ->
            githubLoader.loadBase(repoFullName, branchName, fileName)
        }
    }

    override fun saveNewPullRequest(payload: String): PullRequest =
        super.saveNewPullRequest(payload).also(githubLoader::loadFilesOfCommit)


    private val logger = KotlinLogging.logger {}
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    override val JsonObject.gitService: GitProperty
        get() = GITHUB

    override val JsonObject.number: Int?
        get() = int("number")

    override val JsonObject.creatorName: String?
        get() = obj("pull_request")?.obj("head")?.obj("user")?.string("login")

    override val JsonObject.sourceRepoId: Long?
        get() = obj("pull_request")?.obj("head")?.obj("repo")?.long("id")

    override val JsonObject.mainRepoId: Long?
        get() = obj("pull_request")?.obj("base")?.obj("repo")?.long("id")

    override val JsonObject.sourceRepoFullName: String?
        get() = obj("pull_request")?.obj("head")?.obj("repo")?.string("full_name")

    override val JsonObject.mainRepoFullName: String?
        get() = obj("pull_request")?.obj("base")?.obj("repo")?.string("full_name")

    override val JsonObject.sourceHeadSha: String?
        get() = obj("pull_request")?.obj("head")?.string("sha")

    override val JsonObject.sourceBranchName: String?
        get() = obj("pull_request")?.obj("head")?.string("ref")

    override val JsonObject.mainBranchName: String?
        get() = obj("pull_request")?.obj("base")?.string("ref")

    override val JsonObject.date: LocalDateTime?
        get() = LocalDateTime.parse(
            obj("pull_request")?.string("updated_at")?.substring(0, 19),
            dateFormatter
        )
}