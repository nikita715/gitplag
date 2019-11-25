package io.gitplag.git.payload

import com.beust.klaxon.JsonObject
import io.gitplag.git.agent.GitAgent
import io.gitplag.model.dto.InputRepositoryDto
import io.gitplag.model.entity.Branch
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.BranchRepository
import io.gitplag.model.repo.PullRequestRepository
import io.gitplag.util.parseAsObject
import mu.KotlinLogging
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Common implementation of the [GitManager]
 */
abstract class AbstractGitManager(
    private val pullRequestRepository: PullRequestRepository,
    private val repositoryDataManager: RepositoryDataManager,
    private val gitAgent: GitAgent,
    private val branchRepository: BranchRepository
) : GitManager {
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
            logger.info { "Webhook: ignored pull request to unknown repo $mainRepoFullName" }
        }
    }

    override fun createRepo(dto: InputRepositoryDto): Repository? {
        val repoId = gitAgent.getRepoIdByName(dto.name)
        return if (repoId != null) {
            val repository = repositoryDataManager.create(repoId, dto)
            repositoryDataManager.save(repository.copy(gitId = repoId))
        } else null
    }

    override fun downloadBasesOfRepository(payload: String) {
        val jsonObject = payload.parseAsObject()
        val branchName = requireNotNull(jsonObject.pushBranchName)
        val repoFullName = requireNotNull(jsonObject.pushRepoName)
        val updatedAt = requireNotNull(jsonObject.pushLastUpdated)
        val repo = repositoryDataManager.findByGitServiceAndName(GitProperty.GITHUB, repoFullName)

        if (repo != null) {
            if (!repo.autoCloningEnabled) {
                logger.info {
                    "Webhook: cloning disabled, ignored new push from repo ${repo.name} to branch $branchName"
                }
                return
            }
            logger.info { "Webhook: received new push from repo ${repo.name}" }
            gitAgent.cloneRepository(repo, branchName)
            val branch = branchRepository.findByRepositoryAndName(repo, branchName)
                ?.copy(updatedAt = updatedAt)
            if (branch != null) branchRepository.save(branch) else branchRepository.save(
                Branch(updatedAt = updatedAt, repository = repo, name = branchName)
            )
        } else {
            logger.info { "Webhook: ignored push to unknown repo ${jsonObject.pushRepoName}" }
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
            logger.info { "Webhook: received updated pr from repo ${prJsonObject.mainRepoFullName}, pr number ${pullRequest.number}" }
            val savedPullRequest = pullRequestRepository.save(pullRequest)
            gitAgent.clonePullRequest(savedPullRequest)
        } else {
            val pullRequest = parsePullRequest(prJsonObject, repo)
            if (pullRequest != null && pullRequest.sourceRepoFullName != repo.name) {
                logger.info { "Webhook: received new pr from repo ${prJsonObject.mainRepoFullName}, pr number ${pullRequest.number}" }
                val savedPullRequest = pullRequestRepository.save(pullRequest)
                gitAgent.clonePullRequest(savedPullRequest)
            } else {
                logger.info { "Webhook: Ignored pr to itself repo ${repo.name}, pr number $prNumber" }
            }
        }
    }

    override fun downloadAllPullRequestsOfRepository(repo: Repository) {
        gitAgent.findBranchesOfRepo(repo).forEach { branchName ->
            val lastUpdated =
                requireNotNull(gitAgent.getBranchOfRepo(repo, branchName).branchUpdatedAt)
            val branch = branchRepository.findByRepositoryAndName(repo, branchName)
            if (branch?.updatedAt != lastUpdated) {
                gitAgent.cloneRepository(repo, branchName)
            }
            branchRepository.save(
                branch?.copy(updatedAt = lastUpdated) ?: Branch(
                    updatedAt = lastUpdated,
                    repository = repo, name = branchName
                )
            )
        }
        var page = 1
        while (true) {
            val pullRequests = gitAgent.findPullRequests(repo, page++)
            if (pullRequests.isNotEmpty()) {
                downloadPullRequests(repo, pullRequests)
            } else break
        }
    }

    private fun downloadPullRequests(repo: Repository, pullRequestsJsons: Collection<JsonObject>) {
        pullRequestsJsons.forEach { pullRequestJson ->
            if (prFromTheSameRepo(pullRequestJson)) {
                logger.info { "Webhook: Ignored pr to itself repo ${repo.name} number ${pullRequestJson.number}" }
            } else {
                clonePullRequestIfRequired(pullRequestJson, repo)
            }
        }
    }

    private fun clonePullRequestIfRequired(json: JsonObject, repo: Repository) {
        val storedPullRequest = json.number?.let { pullRequestRepository.findByRepoAndNumber(repo, it) }
        if (storedPullRequest != null && storedPullRequest.updatedAt == json.updatedAt) return
        val pullRequest = parsePullRequest(json, repo)
        if (pullRequest != null && hasNoDuplicates(repo, pullRequest)) {
            val savedPullRequest = if (storedPullRequest == null) {
                pullRequestRepository.save(pullRequest)
            } else pullRequestRepository.save(storedPullRequest.updateFrom(json))
            try {
                gitAgent.clonePullRequest(savedPullRequest)
            } catch (e: FileNotFoundException) {
                logger.info { "Git: unable to download archive ${repo.name}, pr number ${savedPullRequest.number}" }
                pullRequestRepository.delete(savedPullRequest)
            }
            logger.info { "Git: cloned new pr from repo ${repo.name}, pr number ${savedPullRequest.number}" }
        }
    }

    private fun hasNoDuplicates(
        repo: Repository,
        pullRequest: PullRequest
    ): Boolean {
        val duplicatedPullRequest = pullRequestRepository.findByRepoAndCreatorNameAndSourceBranchName(
            repo = repo,
            creatorName = pullRequest.creatorName,
            sourceBranchName = pullRequest.sourceBranchName
        )

        if (duplicatedPullRequest != null) {
            if (duplicatedPullRequest.updatedAt < pullRequest.updatedAt) {
                logger.info {
                    "Git: Deleted duplicated pr #${duplicatedPullRequest.number}," +
                            " another is #${pullRequest.number}, repo ${repo.name}"
                }
                gitAgent.deletePullRequestFiles(duplicatedPullRequest)
                pullRequestRepository.delete(duplicatedPullRequest)
            } else {
                logger.info {
                    "Git: Skipped duplicated pr #${pullRequest.number} " +
                            "of #${duplicatedPullRequest.number}, repo ${repo.name}"
                }
                return false
            }
        }
        return true
    }

    private fun PullRequest.updateFrom(jsonPayload: JsonObject): PullRequest {
        jsonPayload.apply {
            return copy(
                headSha = requireNotNull(sourceHeadSha),
                updatedAt = requireNotNull(updatedAt)
            )
        }
    }

    private fun parsePullRequest(json: JsonObject, repo: Repository): PullRequest? =
        json.run {
            try {
                PullRequest(
                    number = requireNotNull(number),
                    creatorName = requireNotNull(creatorName),
                    sourceRepoFullName = requireNotNull(sourceRepoFullName),
                    repo = repo,
                    headSha = requireNotNull(sourceHeadSha),
                    sourceBranchName = requireNotNull(sourceBranchName),
                    mainBranchName = requireNotNull(mainBranchName),
                    createdAt = requireNotNull(createdAt),
                    updatedAt = requireNotNull(updatedAt)
                )
            } catch (e: IllegalArgumentException) {
                logger.error { "Git: unable to load pr number $number to repo ${repo.name}, git ${repo.gitService}" }
                null
            }
        }

    protected open fun prFromTheSameRepo(json: JsonObject) = json.run { mainRepoFullName == sourceRepoFullName }

    protected abstract val dateFormatter: DateTimeFormatter
    protected fun String?.parseDate() = LocalDateTime.parse(this?.substring(0, 19)?.replace("T", " "), dateFormatter)

    protected abstract val git: GitProperty

    protected abstract val JsonObject.pullRequest: JsonObject

    protected abstract val JsonObject?.sourceRepoId: String?

    protected abstract val JsonObject?.number: Int?

    protected abstract val JsonObject?.mainRepoFullName: String?

    protected abstract val JsonObject?.creatorName: String?

    protected abstract val JsonObject?.sourceHeadSha: String?

    protected abstract val JsonObject?.sourceBranchName: String?

    protected abstract val JsonObject?.createdAt: LocalDateTime?

    protected abstract val JsonObject?.updatedAt: LocalDateTime?

    protected abstract val JsonObject?.sourceRepoFullName: String?

    protected abstract val JsonObject?.mainBranchName: String?

    protected abstract val JsonObject?.mainRepoId: String?

    protected abstract val JsonObject.pushRepoName: String?

    protected abstract val JsonObject.pushBranchName: String?

    protected abstract val JsonObject.pushRepoId: String?

    protected abstract val JsonObject.pushLastUpdated: LocalDateTime?

    protected abstract val JsonObject.branchUpdatedAt: LocalDateTime?
}