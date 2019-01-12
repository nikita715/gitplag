package ru.nikstep.redink.service

import mu.KotlinLogging
import ru.nikstep.redink.data.AnalysisResultData
import ru.nikstep.redink.data.GithubAnalysisConclusion
import ru.nikstep.redink.data.GithubAnalysisStatus
import ru.nikstep.redink.data.PullRequestData

class EmptyPlagiarismService(
    private val analysisResultService: AnalysisResultService
) : PlagiarismService {

    private val logger = KotlinLogging.logger {}

    override fun analyze(data: PullRequestData) {

        logger.info { "Analysis: analysing pull request of user ${data.creatorName}, repo ${data.repoFullName}" }

        analysisResultService.send(
            data,
            AnalysisResultData(
                status = GithubAnalysisStatus.COMPLETED.value,
                conclusion = GithubAnalysisConclusion.SUCCESS.value
            )
        )
    }
}