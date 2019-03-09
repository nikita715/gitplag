package ru.nikstep.redink.analysis

import mu.KotlinLogging
import org.springframework.context.ApplicationListener
import ru.nikstep.redink.analysis.analyser.Analyser
import ru.nikstep.redink.analysis.loader.GitLoader
import ru.nikstep.redink.checks.github.AnalysisStatusCheckService
import ru.nikstep.redink.checks.github.GithubAnalysisConclusion
import ru.nikstep.redink.checks.github.GithubAnalysisResultData
import ru.nikstep.redink.checks.github.GithubAnalysisStatus
import ru.nikstep.redink.model.PullRequestEvent
import ru.nikstep.redink.model.data.AnalysisResultRepository
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.GitProperty
import java.io.PrintWriter
import java.io.StringWriter


class PullRequestListener(
    private val analysisResultRepository: AnalysisResultRepository,
    private val analysisStatusCheckService: AnalysisStatusCheckService,
    private val gitLoaders: Map<GitProperty, GitLoader>,
    private val analysers: Map<AnalyserProperty, Analyser>
) : ApplicationListener<PullRequestEvent> {
    private val logger = KotlinLogging.logger {}

    override fun onApplicationEvent(event: PullRequestEvent) = initiateAnalysis(event.pullRequest)

    private fun initiateAnalysis(pullRequest: PullRequest) {
        val gitServiceLoader = gitLoaders.getValue(pullRequest.gitService)
        val analysisService = analysers.getValue(AnalyserProperty.JPLAG)
        try {
            logger.loggedAnalysis(pullRequest) {
                gitServiceLoader.loadFilesFromGit(pullRequest)
                analysisService.analyse(pullRequest)
                    .also(analysisResultRepository::saveAll)
                    .forEach { sendSuccessStatusCheck(pullRequest) }
            }
        } catch (e: Exception) {
            logger.exceptionAtAnalysisOf(e, pullRequest)
            sendFailureStatusCheck(e, pullRequest)
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