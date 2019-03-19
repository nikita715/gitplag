package ru.nikstep.redink.git.webhook

import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.git.loader.GithubLoader
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.GitProperty.GITHUB
import ru.nikstep.redink.util.RepositoryNotFoundException
import ru.nikstep.redink.util.parseAsObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of the [AbstractWebhookService] for handling Github webhooks
 */
class GithubWebhookService(
    pullRequestRepository: PullRequestRepository,
    private val repositoryRepository: RepositoryRepository,
    private val githubLoader: GithubLoader
) : AbstractWebhookService(pullRequestRepository, repositoryRepository, githubLoader) {

    private val logger = KotlinLogging.logger {}
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    override val git = GITHUB

    override fun saveNewBaseFiles(payload: String) {
        val jsonObject = payload.parseAsObject()
        val branchName = requireNotNull(jsonObject.string("ref")?.substringAfterLast("/"))
        val repoFullName = requireNotNull(jsonObject.obj("repository")?.string("full_name"))
        val repo = repositoryRepository.findByGitServiceAndName(GITHUB, repoFullName)
            ?: throw RepositoryNotFoundException()
        githubLoader.loadBaseBranch(repo, branchName)
    }

    fun saveNewRepository(payload: String) {
        val jsonObject = payload.parseAsObject()
        val repoName = requireNotNull(jsonObject.obj("repository")?.string("full_name"))
        val repo = repositoryRepository.findByGitServiceAndName(GITHUB, repoName)
        if (repo == null) {
            val repository = repositoryRepository.save(Repository(gitService = GITHUB, name = repoName))
            logger.info { "Webhook: Saved new repository with id = ${repository.id}, name = ${repository.name}" }
            githubLoader.cloneRepositoryAndPullRequests(repository)
        }
    }

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