package ru.nikstep.redink.git.loader

import mu.KotlinLogging
import ru.nikstep.redink.analysis.AnalysisException
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest

/**
 * Common implementation of the [GitLoader]
 */
abstract class AbstractGitLoader(
    private val solutionStorage: SolutionStorage
) : GitLoader {

    private val logger = KotlinLogging.logger {}

    override fun loadFilesOfCommit(pullRequest: PullRequest) {
        val changedFiles = loadChangedFilesOfCommit(pullRequest.sourceRepoFullName, pullRequest.headSha)

        changedFiles.forEach { fileName ->
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

    protected abstract fun loadChangedFiles(pullRequest: PullRequest): List<String>

    protected abstract fun loadChangedFilesOfCommit(repoName: String, headSha: String): List<String>

}