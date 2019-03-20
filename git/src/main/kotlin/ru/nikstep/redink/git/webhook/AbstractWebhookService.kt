package ru.nikstep.redink.git.webhook

import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.git.loader.GitLoader
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.parseAsObject
import java.time.LocalDateTime

/**
 * Common implementation of the [WebhookService]
 */
abstract class AbstractWebhookService(
    private val pullRequestRepository: PullRequestRepository,
    private val repositoryRepository: RepositoryRepository,
    private val gitLoader: GitLoader
) : WebhookService {
    private val logger = KotlinLogging.logger {}

    override fun updateSolutionsOfPullRequest(payload: String) {
        val jsonObject = payload.parseAsObject()

        val mainRepoFullName = jsonObject.pullRequest.mainRepoFullName
        val repo = repositoryRepository.findByGitServiceAndName(
            git, requireNotNull(mainRepoFullName)
        )

        if (repo != null) {
            logger.info { "Webhook: received new pull request from repo ${repo.name}" }
            cloneReceivedPullRequest(jsonObject, repo)
        } else {
            logger.info { "Webhook: received new repo $mainRepoFullName" }
            cloneRepoAndAllPullRequests(mainRepoFullName, jsonObject.pullRequest.mainRepoId)
        }
    }

    override fun updateBasesOfRepository(payload: String) {
        val jsonObject = payload.parseAsObject()
        val branchName = requireNotNull(jsonObject.pushBranchName)
        val repoFullName = requireNotNull(jsonObject.pushRepoName)
        val repo = repositoryRepository.findByGitServiceAndName(GitProperty.GITHUB, repoFullName)

        if (repo != null) {
            logger.info { "Webhook: received new push from repo ${repo.name}" }
            gitLoader.cloneRepository(repo, branchName)
        } else {
            logger.info { "Webhook: received new repo ${jsonObject.pushRepoName}" }
            cloneRepoAndAllPullRequests(jsonObject.pushRepoName, jsonObject.pushRepoId)
        }
    }

    abstract val JsonObject.pushRepoName: String?
    abstract val JsonObject.pushBranchName: String?
    abstract val JsonObject.pushRepoId: Long?

    private fun cloneReceivedPullRequest(
        jsonObject: JsonObject,
        repo: Repository
    ) {
        val pullRequest = jsonObject.pullRequest.run { parsePullRequest(repo) }
        if (pullRequest.sourceRepoFullName != jsonObject.mainRepoFullName) {
            gitLoader.clonePullRequest(pullRequest)
        } else {
            logger.info { "Webhook: Ignored pr to itself repo ${repo.name}" }
        }
    }

    private fun cloneRepoAndAllPullRequests(repoName: String?, repoId: Long?) {
        val repo = repositoryRepository.save(
            Repository(
                name = requireNotNull(repoName),
                gitService = git,
                gitId = requireNotNull(repoId)
            )
        )
        gitLoader.cloneRepository(repo)
        gitLoader.findPullRequests(repo).forEach {
            it.run {
                if (mainRepoFullName != sourceRepoFullName) {
                    val pullRequest = pullRequestRepository.save(parsePullRequest(repo))
                    gitLoader.clonePullRequest(pullRequest)
                } else {
                    logger.info { "Webhook: Ignored pr to itself repo ${repo.name}" }
                }
            }
        }
    }

    private fun JsonObject.parsePullRequest(repo: Repository): PullRequest =
        PullRequest(
            number = requireNotNull(number),
            creatorName = requireNotNull(creatorName),
            sourceRepoId = requireNotNull(sourceRepoId),
            mainRepoId = requireNotNull(mainRepoId),
            sourceRepoFullName = requireNotNull(sourceRepoFullName),
            repo = repo,
            headSha = requireNotNull(sourceHeadSha),
            sourceBranchName = requireNotNull(sourceBranchName),
            mainBranchName = requireNotNull(mainBranchName),
            date = requireNotNull(date)
        )

    protected abstract val git: GitProperty

    protected abstract val JsonObject.pullRequest: JsonObject

    protected abstract val JsonObject?.sourceRepoId: Long?

    protected abstract val JsonObject?.number: Int?

    protected abstract val JsonObject?.mainRepoFullName: String?

    protected abstract val JsonObject?.creatorName: String?

    protected abstract val JsonObject?.sourceHeadSha: String?

    protected abstract val JsonObject?.sourceBranchName: String?

    protected abstract val JsonObject?.date: LocalDateTime?

    protected abstract val JsonObject?.sourceRepoFullName: String?

    protected abstract val JsonObject?.mainBranchName: String?

    protected abstract val JsonObject?.mainRepoId: Long?
}