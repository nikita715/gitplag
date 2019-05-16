package io.gitplag.git.rest

import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.util.downloadAndUnpackZip
import mu.KotlinLogging
import java.io.File

/**
 * Common implementation of the [GitRestManager]
 */
abstract class AbstractGitRestManager(
    private val sourceCodeStorage: SourceCodeStorage
) : GitRestManager {

    private val logger = KotlinLogging.logger {}

    override fun clonePullRequest(pullRequest: PullRequest) {
        val resourceUrl = linkToRepoArchive(pullRequest.sourceRepoFullName, pullRequest.sourceBranchName)
        logger.info { "Git: trying to download the zip from url $resourceUrl" }
        downloadAndUnpackZip(resourceUrl) { unpackedDir ->
            val sourceDir = File(unpackedDir).listFiles()[0].absolutePath
            sourceCodeStorage.saveSolutionsFromDir(
                sourceDir, pullRequest
            )
        }
        logger.info { "Git: downloaded and unpacked the zip from url $resourceUrl" }
    }

    override fun deletePullRequestFiles(pullRequest: PullRequest) {
        pullRequest.solutionFiles.forEach { file ->
            sourceCodeStorage.deleteSolutionFile(
                pullRequest.repo,
                pullRequest.sourceBranchName,
                pullRequest.creatorName,
                file.fileName
            )
        }
        logger.info { "Git: deleted duplicated pull request #${pullRequest.id} of repo ${pullRequest.repo.name}" }
    }

    override fun cloneRepository(repo: Repository, branch: String?) {
        if (branch == null) {
            findBranchesOfRepo(repo).forEach { cloneBranchOfRepository(repo, it) }
        } else {
            cloneBranchOfRepository(repo, branch)
        }
    }

    protected abstract fun linkToRepoArchive(repoName: String, branchName: String): String

    private fun cloneBranchOfRepository(repo: Repository, branch: String) {
        logger.info { "Git: download zip archive of repo = ${repo.name}, branch = $branch" }
        downloadAndUnpackZip(linkToRepoArchive(repo.name, branch)) { unpackedDir ->
            val sourceDir = File(unpackedDir).listFiles()[0].absolutePath
            sourceCodeStorage.saveBasesFromDir(
                sourceDir,
                repo, branch
            )
        }
    }
}