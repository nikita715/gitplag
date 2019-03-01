package ru.nikstep.redink.analysis.loader

import mu.KotlinLogging
import ru.nikstep.redink.analysis.AnalysisException
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.StringDeserializer
import ru.nikstep.redink.util.sendRestRequest
import java.io.File

abstract class AbstractGitServiceLoader(
    private val solutionStorage: SolutionStorage,
    private val repositoryRepository: RepositoryRepository
) : GitServiceLoader {

    private val logger = KotlinLogging.logger {}
    private val masterBranch = "master"

    override fun loadFilesFromGit(pullRequest: PullRequest) {
        val filePatterns = repositoryRepository.findByName(pullRequest.repoFullName).filePatterns
        val changedFiles = pullRequest.changedFiles.intersect(filePatterns)

        filePatterns.intersect(changedFiles).forEach { fileName ->
            checkBaseExists(pullRequest, fileName)

            val fileText = sendRestRequest(
                getFileQuery(pullRequest.repoFullName, pullRequest.headSha, fileName),
                deserializer = StringDeserializer
            )

            if (fileText.isBlank())
                throw AnalysisException("$fileName is not found in ${pullRequest.branchName} branch, ${pullRequest.repoFullName} repo")

            solutionStorage.saveSolution(pullRequest, fileName, fileText)

            logger.info {
                "Analysis: loaded file ${pullRequest.repoFullName}/$fileName" +
                        " of user ${pullRequest.creatorName}, pr number ${pullRequest.number}"
            }
        }
    }

    abstract fun getFileQuery(repoName: String, branchName: String, fileName: String): String

    private fun checkBaseExists(data: PullRequest, fileName: String) {
        val base = solutionStorage.loadBase(data.repoFullName, fileName)
        if (base.notExists()) saveBase(data, fileName)
    }

    private fun saveBase(pullRequest: PullRequest, fileName: String) {
        val fileText = sendRestRequest(
            getFileQuery(pullRequest.repoFullName, masterBranch, fileName),
            deserializer = StringDeserializer
        )

        if (fileText.isBlank())
            throw AnalysisException("$fileName is not found in ${pullRequest.branchName} branch, ${pullRequest.repoFullName} repo")

        solutionStorage.saveBase(pullRequest, fileName, fileText)

        logger.info { "Analysis: loaded base file ${pullRequest.repoFullName}/$fileName" }
    }

    private fun File.notExists(): Boolean {
        return !this.exists()
    }

}