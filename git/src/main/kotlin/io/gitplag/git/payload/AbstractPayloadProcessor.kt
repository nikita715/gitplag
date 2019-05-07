package io.gitplag.git.payload

import com.beust.klaxon.JsonObject
import io.gitplag.git.rest.GitRestManager
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.PullRequestRepository
import io.gitplag.util.parseAsObject
import mu.KotlinLogging
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Common implementation of the [PayloadProcessor]
 */
abstract class AbstractPayloadProcessor(
    private val pullRequestRepository: PullRequestRepository,
    private val repositoryDataManager: RepositoryDataManager,
    private val gitRestManager: GitRestManager
) : PayloadProcessor {
    private val logger = KotlinLogging.logger {}

    override fun downloadSolutionsOfPullRequest(payload: String) {
        val jsonObject = payload.parseAsObject()

        val mainRepoFullName = jsonObject.pullRequest.mainRepoFullName
        val repo = repositoryDataManager.findByGitServiceAndName(
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

    override fun downloadBasesOfRepository(payload: String) {
        val jsonObject = payload.parseAsObject()
        val branchName = requireNotNull(jsonObject.pushBranchName)
        val repoFullName = requireNotNull(jsonObject.pushRepoName)
        val repo = repositoryDataManager.findByGitServiceAndName(GitProperty.GITHUB, repoFullName)

        if (repo != null) {
            if (!repo.autoCloningEnabled) {
                logger.info {
                    "Webhook: cloning disabled, " +
                            "ignored new push from repo ${repo.name} to branch $branchName"
                }
                return
            }
            logger.info { "Webhook: received new push from repo ${repo.name}" }
            gitRestManager.cloneRepository(repo, branchName)
        } else {
            logger.info { "Webhook: received new repo ${jsonObject.pushRepoName}" }
            cloneRepoAndAllPullRequests(jsonObject.pushRepoName, jsonObject.pushRepoId)
        }
    }

    private fun cloneReceivedPullRequest(
        jsonObject: JsonObject,
        repo: Repository
    ) {
        val prJsonObject = jsonObject.pullRequest
        val prNumber = requireNotNull(prJsonObject.number)
        val storedPullRequest = pullRequestRepository.findByRepoAndNumber(repo, prNumber)
        if (storedPullRequest != null) {
            if (!repo.autoCloningEnabled) {
                logger.info { "Webhook: cloning disabled, ignored new push from repo ${repo.name} to branch ${prJsonObject.sourceBranchName}" }
                return
            }
            val pullRequest = storedPullRequest.updateFrom(prJsonObject)
            logger.info { "Webhook: received updated pr from repo ${prJsonObject.pushRepoName}, pr number ${pullRequest.number}" }
            gitRestManager.clonePullRequest(pullRequest)
            pullRequestRepository.save(pullRequest)
        } else {
            val pullRequest = prJsonObject.run { parsePullRequest(repo) }
            if (pullRequest != null && pullRequest.sourceRepoFullName != prJsonObject.mainRepoFullName) {
                logger.info { "Webhook: received new pr from repo ${prJsonObject.pushRepoName}, pr number ${pullRequest.number}" }
                gitRestManager.clonePullRequest(pullRequest)
                pullRequestRepository.save(pullRequest)
            } else {
                logger.info { "Webhook: Ignored pr to itself repo ${repo.name}, pr number $prNumber" }
            }
        }
    }

    private fun cloneRepoAndAllPullRequests(repoName: String?, repoId: Long?) {
        val repo = repositoryDataManager.save(
            Repository(
                name = requireNotNull(repoName),
                gitService = git,
                gitId = requireNotNull(repoId)
            )
        )
        gitRestManager.cloneRepository(repo)
        downloadAllPullRequestsOfRepository(repo)
    }

    override fun downloadAllPullRequestsOfRepository(repo: Repository) {
        var page = 1
        while (true) {
            val pullRequests = gitRestManager.findPullRequests(repo, page++)
            if (pullRequests.isNotEmpty()) {
                downloadPullRequests(repo, pullRequests)
            } else break
        }
    }

    private fun downloadPullRequests(repo: Repository, pullRequestsJsons: Collection<JsonObject>) {
        pullRequestsJsons.forEach { pullRequestJson ->
            pullRequestJson.run {
                if (mainRepoFullName == sourceRepoFullName) {
                    logger.info { "Webhook: Ignored pr to itself repo ${repo.name}" }
                } else {
                    val pullRequest = parsePullRequest(repo)
                    if (pullRequest != null) {
                        val savedPullRequest = pullRequestRepository.save(pullRequest)
                        gitRestManager.clonePullRequest(savedPullRequest)
                        logger.info { "Webhook: cloned new pr from repo ${repo.name}, pr number ${savedPullRequest.number}" }
                    }
                }
            }

        }
    }

    private fun PullRequest.updateFrom(jsonPayload: JsonObject): PullRequest {
        jsonPayload.apply {
            return copy(
                headSha = requireNotNull(sourceHeadSha),
                updatedAt = requireNotNull(updatedAt)
            )
        }
    }

    private fun JsonObject.parsePullRequest(repo: Repository): PullRequest? =
        try {
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
                createdAt = requireNotNull(createdAt),
                updatedAt = requireNotNull(updatedAt)
            )
        } catch (e: IllegalArgumentException) {
            logger.error { "Webhook: unable to load pr number $number to repo ${repo.name}, git ${repo.gitService}" }
            null
        }

    protected abstract val dateFormatter: DateTimeFormatter
    protected fun String?.parseDate() = LocalDateTime.parse(this?.substring(0, 19), dateFormatter)

    protected abstract val git: GitProperty

    protected abstract val JsonObject.pullRequest: JsonObject

    protected abstract val JsonObject?.sourceRepoId: Long?

    protected abstract val JsonObject?.number: Int?

    protected abstract val JsonObject?.mainRepoFullName: String?

    protected abstract val JsonObject?.creatorName: String?

    protected abstract val JsonObject?.sourceHeadSha: String?

    protected abstract val JsonObject?.sourceBranchName: String?

    protected abstract val JsonObject?.createdAt: LocalDateTime?

    protected abstract val JsonObject?.updatedAt: LocalDateTime?

    protected abstract val JsonObject?.sourceRepoFullName: String?

    protected abstract val JsonObject?.mainBranchName: String?

    protected abstract val JsonObject?.mainRepoId: Long?

    protected abstract val JsonObject.pushRepoName: String?

    protected abstract val JsonObject.pushBranchName: String?

    protected abstract val JsonObject.pushRepoId: Long?
}