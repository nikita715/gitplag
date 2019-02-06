package ru.nikstep.redink.analysis

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import ru.nikstep.redink.checks.AnalysisResultData
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.checks.GithubAnalysisConclusion
import ru.nikstep.redink.checks.GithubAnalysisStatus
import ru.nikstep.redink.model.data.AnalysisResultRepository
import ru.nikstep.redink.model.repo.PullRequestRepository

open class AnalysisScheduler(
    private val pullRequestRepository: PullRequestRepository,
    private val analysisService: AnalysisService,
    private val analysisResultRepository: AnalysisResultRepository,
    private val analysisStatusCheckService: AnalysisStatusCheckService
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(fixedDelay = 10000)
    fun runAnalysis() {
        val countOfRequiredAnalyses = pullRequestRepository.countAllByAnalysedIsFalse()

        if (countOfRequiredAnalyses.compareTo(0) == 0) {
            logger.info { "Analysis: no required analyses" }
            return
        } else {
            logger.info { "Analysis: start analysis of $countOfRequiredAnalyses new pull request(s)" }
        }

        val pullRequestList = pullRequestRepository.findAllByAnalysedIsFalse()
        for (i in 0 until pullRequestList.size) {
            val pullRequest = pullRequestList[i]
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

            logger.info { "Analysis: analysed ${pullRequestList.size} pull requests" }
        }
    }

}