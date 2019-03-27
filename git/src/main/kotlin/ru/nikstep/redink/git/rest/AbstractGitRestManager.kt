package ru.nikstep.redink.git.rest

import mu.KotlinLogging
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.downloadAndUnpackZip
import java.io.File

/**
 * Common implementation of the [GitRestManager]
 */
abstract class AbstractGitRestManager(
    private val solutionStorage: SolutionStorage
) : GitRestManager {

    private val logger = KotlinLogging.logger {}

    override fun clonePullRequest(pullRequest: PullRequest) {
        val resourceUrl = linkToRepoArchive(pullRequest.sourceRepoFullName, pullRequest.sourceBranchName)
        downloadAndUnpackZip(resourceUrl) { unpackedDir ->
            val sourceDir = File(unpackedDir).listFiles()[0].absolutePath
            solutionStorage.saveSolutionsFromDir(
                sourceDir, pullRequest
            )
        }
    }

    override fun cloneRepository(repo: Repository, branch: String?) {
        if (branch == null) {
            findBranchesOfRepo(repo).forEach { cloneBranchOfRepository(repo, it) }
        } else {
            cloneBranchOfRepository(repo, branch)
        }
    }

    protected abstract fun linkToRepoArchive(repoName: String, branchName: String): String

    protected abstract fun findBranchesOfRepo(repo: Repository): List<String>

    private fun cloneBranchOfRepository(repo: Repository, branch: String) {
        logger.info { "Git: download zip archive of repo = ${repo.name}, branch = $branch" }
        downloadAndUnpackZip(linkToRepoArchive(repo.name, branch)) { unpackedDir ->
            val sourceDir = File(unpackedDir).listFiles()[0].absolutePath
            solutionStorage.saveBasesFromDir(
                sourceDir,
                repo, branch
            )
        }
    }
}