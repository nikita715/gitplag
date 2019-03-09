package ru.nikstep.redink.analysis.loader

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
        val filePatterns = repositoryRepository.findByName(pullRequest.repoFullName).filePatterns
        val changedFiles = loadChangedFiles(pullRequest)

        filePatterns.intersect(changedFiles).forEach { fileName ->
            checkBaseExists(pullRequest, fileName)

            val fileText = loadFileText(pullRequest.repoFullName, pullRequest.branchName, fileName)

            if (fileText.isBlank())
                throw AnalysisException("$fileName is not found in ${pullRequest.branchName} branch, ${pullRequest.repoFullName} repo")

            solutionStorage.saveSolution(pullRequest, fileName, fileText)

            logger.info {
                "Analysis: loaded file ${pullRequest.repoFullName}/$fileName" +
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
        val base = solutionStorage.loadBase(data.repoFullName, fileName)
        if (base.notExists()) saveBase(data, fileName)
    }

    private fun saveBase(pullRequest: PullRequest, fileName: String) {
        val fileText = loadFileText(pullRequest.repoFullName, masterBranch, fileName, pullRequest.secretKey)

        if (fileText.isBlank())
            throw AnalysisException("$fileName is not found in ${pullRequest.branchName} branch, ${pullRequest.repoFullName} repo")

        solutionStorage.saveBase(pullRequest, fileName, fileText)

        logger.info { "Analysis: loaded base file ${pullRequest.repoFullName}/$fileName" }
    }

    private fun File.notExists(): Boolean {
        return !this.exists()
    }

}