package ru.nikstep.redink.analysis.loader

import mu.KotlinLogging
import ru.nikstep.redink.analysis.AnalysisException
import ru.nikstep.redink.analysis.solutions.SolutionStorageService
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.RequestUtil.Companion.sendRestRequest
import ru.nikstep.redink.util.StringDeserializer
import java.io.File

class BitbucketServiceLoader(
    private val solutionStorageService: SolutionStorageService,
    private val repositoryRepository: RepositoryRepository
) : GitServiceLoader {

    private val logger = KotlinLogging.logger {}

    private val bitbucketFileQuery = "https://bitbucket.org/%s/raw/%s/%s"

    override fun loadFilesFromGit(pullRequest: PullRequest) {

        val fileNames =
            pullRequest.changedFiles.intersect(repositoryRepository.findByName(pullRequest.repoFullName).filePatterns)

        fileNames.map { fileName ->
            checkBaseExists(pullRequest, fileName)

            val fileText = sendRestRequest(
                url = String.format(
                    bitbucketFileQuery,
                    pullRequest.repoFullName,
                    pullRequest.branchName,
                    fileName
                ),
                deserializer = StringDeserializer()
            ) as String

            if (fileText.isBlank())
                throw AnalysisException("$fileName is not found in ${pullRequest.branchName} branch, ${pullRequest.repoFullName} repo")

            val solution = solutionStorageService.saveSolution(pullRequest, fileName, fileText)

            logger.info {
                "Analysis: loaded file ${pullRequest.repoFullName}/$fileName" +
                        " of user ${pullRequest.creatorName}, pr number ${pullRequest.number}"
            }

            solution
        }
    }

    private fun checkBaseExists(data: PullRequest, fileName: String) {
        val base = solutionStorageService.loadBase(data.repoFullName, fileName)
        if (base.notExists()) saveBase(data, fileName)
    }

    private fun saveBase(pullRequest: PullRequest, fileName: String) {
        val fileText = sendRestRequest(
            url = String.format(
                bitbucketFileQuery,
                pullRequest.repoFullName,
                "master",
                fileName
            ),
            deserializer = StringDeserializer()
        ) as String

        if (fileText.isBlank())
            throw AnalysisException("$fileName is not found in ${pullRequest.branchName} branch, ${pullRequest.repoFullName} repo")

        solutionStorageService.saveBase(pullRequest, fileName, fileText)

        logger.info { "Analysis: loaded base file ${pullRequest.repoFullName}/$fileName" }
    }
}

private fun File.notExists(): Boolean {
    return !this.exists()
}
