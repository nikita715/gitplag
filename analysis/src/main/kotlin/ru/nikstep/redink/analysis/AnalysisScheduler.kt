package ru.nikstep.redink.analysis

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.data.AnalysisResultData
import ru.nikstep.redink.data.GithubAnalysisConclusion
import ru.nikstep.redink.data.GithubAnalysisStatus
import ru.nikstep.redink.model.repo.AnalysisResultRepository
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

        for (i in 0 until countOfRequiredAnalyses) {
            val data = pullRequestRepository.findFirstByAnalysedIsFalse()!!
            logger.info { "Analysis: start analysing of pr #${data.number}, repo ${data.repoFullName}, user ${data.creatorName}" }

            val analysisResult = analysisService.analyse(data)
            analysisResultRepository.save(analysisResult)

            analysisStatusCheckService.send(
                data,
                AnalysisResultData(
                    status = GithubAnalysisStatus.COMPLETED.value,
                    conclusion = GithubAnalysisConclusion.SUCCESS.value
                )
            )

            data.analysed = true
            pullRequestRepository.save(data)
            logger.info { "Analysis: complete analysing of pr #${data.number}, repo ${data.repoFullName}, user ${data.creatorName}" }

        }
    }

}