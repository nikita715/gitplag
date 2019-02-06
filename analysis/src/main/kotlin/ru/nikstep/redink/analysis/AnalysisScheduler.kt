package ru.nikstep.redink.analysis

import mu.KotlinLogging
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.Scheduled
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
    private val taskExecutor: TaskExecutor
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(fixedDelay = 15000)
    fun runAnalysis() {
        val countOfRequiredAnalyses = pullRequestRepository.countAllByAnalysedIsFalse()

        if (countOfRequiredAnalyses.isZero()) {
            logger.info { "Analysis: waiting for pull requests" }
            return
        } else {
            logger.info { "Analysis: found $countOfRequiredAnalyses created/changed pull request(s)" }
        }

        startAnalysisOfNewPullRequests()
    }

    private fun startAnalysisOfNewPullRequests() {
        val allChangedPullRequests = pullRequestRepository.findAllByAnalysedIsFalse()
        val pullRequestsToAnalyse = allChangedPullRequests.removeDuplicatedPullRequests()

        removeDuplicates(allChangedPullRequests, pullRequestsToAnalyse)

        for (pullRequest in pullRequestsToAnalyse) {
            taskExecutor.execute(AnalysisRunnable(pullRequest))
        }
    }

    private fun removeDuplicates(allChangedPullRequests: List<PullRequest>, pullRequestsToAnalyse: List<PullRequest>) {
        for (duplicatedPullRequest in allChangedPullRequests.minus(pullRequestsToAnalyse)) {
            duplicatedPullRequest.analysed = true
            pullRequestRepository.save(duplicatedPullRequest)
        }
    }

    inner class AnalysisRunnable(private val pullRequest: PullRequest) : Runnable {

        override fun run() {

            try {
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

                pullRequest.analysed = true
                pullRequestRepository.save(pullRequest)
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

private fun Int.isZero(): Boolean {
    return this.compareTo(0) == 0
}

private fun List<PullRequest>.removeDuplicatedPullRequests(): List<PullRequest> {
    return this.sortedByDescending { it.id }.distinctBy { (it.repoFullName to it.creatorName) }
}