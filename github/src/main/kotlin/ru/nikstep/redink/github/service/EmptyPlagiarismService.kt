package ru.nikstep.redink.github.service

import mu.KotlinLogging
import ru.nikstep.redink.github.data.AnalysisResultData
import ru.nikstep.redink.github.data.GithubAnalysisConclusion
import ru.nikstep.redink.github.data.GithubAnalysisStatus
import ru.nikstep.redink.github.data.PullRequestData

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