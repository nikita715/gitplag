package ru.nikstep.redink.git.loader

import mu.KotlinLogging
import ru.nikstep.redink.analysis.AnalysisException
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.RepositoryRepository
import java.io.File

/**
 * Common implementation of the [GitLoader]
 */
abstract class AbstractGitLoader(
    private val solutionStorage: SolutionStorage,
    private val repositoryRepository: RepositoryRepository
) : GitLoader {

    private val logger = KotlinLogging.logger {}
    private val masterBranch = "master"

    override fun loadFilesFromGit(pullRequest: PullRequest) {
//        val filePatterns =
//            repositoryRepository.findByGitServiceAndName(pullRequest.gitService, pullRequest.mainRepoFullName).filePatterns
        val changedFiles = loadChangedFiles(pullRequest)

        changedFiles.forEach { fileName ->
            checkBaseExists(pullRequest, fileName)

            val fileText = loadFileText(pullRequest.sourceRepoFullName, pullRequest.headSha, fileName)

            if (fileText.isBlank())
                throw AnalysisException("$fileName is not found in ${pullRequest.sourceBranchName} branch, ${pullRequest.sourceRepoFullName} repo")

            solutionStorage.saveSolution(pullRequest, fileName, fileText)

            logger.info {
                "Analysis: loaded file ${pullRequest.mainRepoFullName}/$fileName" +
                        " of user ${pullRequest.creatorName}, pr number ${pullRequest.number}"
            }
        }
    }

    override fun loadFileText(
        repoFullName: String,
        branchName: String,
        fileName: String
    ): String {
        return loadFileText(repoFullName, branchName, fileName, "")
    }

    protected abstract fun loadChangedFiles(pullRequest: PullRequest): List<String>

    private fun checkBaseExists(data: PullRequest, fileName: String) {
        val base = solutionStorage.loadBase(data.gitService, data.mainRepoFullName, data.sourceBranchName, fileName)
        if (base.notExists()) saveBase(data, fileName)
    }

    private fun saveBase(pullRequest: PullRequest, fileName: String) {
        try {
            val fileText =
                loadFileText(pullRequest.mainRepoFullName, pullRequest.mainBranchName, fileName, pullRequest.secretKey)

            if (fileText.isBlank())
                throw AnalysisException("$fileName is not found in ${pullRequest.sourceBranchName} branch, ${pullRequest.mainRepoFullName} repo")

            solutionStorage.saveBase(pullRequest, fileName, fileText)

            logger.info { "Analysis: loaded base file ${pullRequest.mainRepoFullName}/$fileName" }
        } catch (e: Exception) {
            logger.info { "No base" }
        }
    }

    private fun File.notExists(): Boolean {
        return !this.exists()
    }

}