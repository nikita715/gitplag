package ru.nikstep.redink.analysis

import mu.KotlinLogging
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.Scheduled
import ru.nikstep.redink.analysis.loader.GitServiceLoader
import ru.nikstep.redink.checks.AnalysisResultData
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.checks.GithubAnalysisConclusion
import ru.nikstep.redink.checks.GithubAnalysisStatus
import ru.nikstep.redink.model.data.AnalysisResultRepository
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository

open class AnalysisScheduler(
    private val pullRequestRepository: PullRequestRepository,
    private val analysisService: AnalysisService,
    private val analysisResultRepository: AnalysisResultRepository,
    private val analysisStatusCheckService: AnalysisStatusCheckService,
    private val gitServiceLoader: GitServiceLoader,
    private val taskExecutor: TaskExecutor
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(fixedDelay = 10000)
    fun runAnalysis() {
        val countOfRequiredAnalyses = pullRequestRepository.count()

        if (countOfRequiredAnalyses.isZero()) {
            logger.info { "Analysis: waiting for pull requests" }
            return
        }

        logger.info { "Analysis: found $countOfRequiredAnalyses created/changed pull request(s)" }
        startAnalysisOfNewPullRequests()
    }

    private fun startAnalysisOfNewPullRequests() {
        val allChangedPullRequests = pullRequestRepository.findAll()
        val pullRequestsToAnalyse = allChangedPullRequests.withoutDuplicates()

        removeDuplicatedPullRequests(allChangedPullRequests.minus(pullRequestsToAnalyse))

        for (pullRequest in pullRequestsToAnalyse) {
            taskExecutor.execute(AnalysisRunnable(pullRequest))
        }
    }

    private fun removeDuplicatedPullRequests(pullRequests: List<PullRequest>) {
        for (pullRequest in pullRequests) {
            logger.info {
                "Analysis: delete duplicated analysis request of pr #${pullRequest.number}," +
                        " repo ${pullRequest.repoFullName}, user ${pullRequest.creatorName}"
            }
            pullRequestRepository.delete(pullRequest)
        }
    }

    inner class AnalysisRunnable(private val pullRequest: PullRequest) : Runnable {

        override fun run() {

            try {
                gitServiceLoader.loadFilesFromGit(pullRequest)

                logger.info {
                    "Analysis: start analysing of pr #${pullRequest.number}," +
                            " repo ${pullRequest.repoFullName}, user ${pullRequest.creatorName}"
                }

                val analysisResult = analysisService.analyse(pullRequest)
                analysisResultRepository.save(analysisResult)

                analysisStatusCheckService.send(
                    pullRequest,
                    AnalysisResultData(
                        status = GithubAnalysisStatus.COMPLETED.value,
                        conclusion = GithubAnalysisConclusion.SUCCESS.value
                    )
                )

                pullRequestRepository.delete(pullRequest)
                logger.info {
                    "Analysis: complete analysing of pr #${pullRequest.number}," +
                            " repo ${pullRequest.repoFullName}, user ${pullRequest.creatorName}"
                }
            } catch (e: Exception) {
                logger.error { "Analysis: exception at the analysis of the pull request with id = ${pullRequest.id}" }
                e.printStackTrace()
            }
        }

    }

}

private fun Long.isZero(): Boolean {
    return this.compareTo(0) == 0
}

private fun List<PullRequest>.withoutDuplicates(): List<PullRequest> {
    return this.sortedByDescending { it.id }.distinctBy { (it.repoFullName to it.creatorName) }
}