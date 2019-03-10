package ru.nikstep.redink.analysis

import mu.KotlinLogging
import ru.nikstep.redink.analysis.analyser.Analyser
import ru.nikstep.redink.checks.github.AnalysisStatusCheckService
import ru.nikstep.redink.checks.github.GithubAnalysisConclusion
import ru.nikstep.redink.checks.github.GithubAnalysisResultData
import ru.nikstep.redink.checks.github.GithubAnalysisStatus
import ru.nikstep.redink.model.data.AnalysisResultRepository
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.GitProperty
import java.io.PrintWriter
import java.io.StringWriter

class AnalysisManager(
    private val analysisStatusCheckService: AnalysisStatusCheckService,
    private val analysers: Map<AnalyserProperty, Analyser>,
    private val analysisResultRepository: AnalysisResultRepository
) {
    private val logger = KotlinLogging.logger {}

    fun initiateAnalysis(repository: Repository) {
        val analysisService = analysers.getValue(AnalyserProperty.JPLAG)
        try {
            logger.loggedAnalysis(repository) {
                analysisService.analyse(repository)
                    .also { analysisResultRepository.saveAll(repository, it) }
            }
        } catch (e: Exception) {
            logger.exceptionAtAnalysisOf(e, repository)
        }
    }

    private fun sendSuccessStatusCheck(pullRequest: PullRequest) {
        if (pullRequest.gitService == GitProperty.GITHUB) {
            analysisStatusCheckService.send(
                pullRequest,
                GithubAnalysisResultData(
                    status = GithubAnalysisStatus.COMPLETED.value,
                    conclusion = GithubAnalysisConclusion.SUCCESS.value
                )
            )
        }
    }

    private fun sendFailureStatusCheck(throwable: Throwable, pullRequest: PullRequest) {
        if (pullRequest.gitService == GitProperty.GITHUB) {
            val outError = StringWriter()
            throwable.printStackTrace(PrintWriter(outError))
            analysisStatusCheckService.send(
                pullRequest,
                GithubAnalysisResultData(
                    status = GithubAnalysisStatus.COMPLETED.value,
                    conclusion = GithubAnalysisConclusion.FAILURE.value,
                    summary = outError.toString()
                )
            )
        }
    }
}