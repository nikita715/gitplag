package ru.nikstep.redink.analysis

import mu.KotlinLogging
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.annotation.Scheduled
import ru.nikstep.redink.analysis.analyser.Analyser
import ru.nikstep.redink.analysis.loader.GitLoader
import ru.nikstep.redink.checks.github.AnalysisStatusCheckService
import ru.nikstep.redink.checks.github.GithubAnalysisConclusion
import ru.nikstep.redink.checks.github.GithubAnalysisResultData
import ru.nikstep.redink.checks.github.GithubAnalysisStatus
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.AnalysisResultRepository
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.GitProperty.GITHUB

open class AnalysisScheduler(
    private val pullRequestRepository: PullRequestRepository,
    private val analysisResultRepository: AnalysisResultRepository,
    private val analysisStatusCheckService: AnalysisStatusCheckService,
    private val taskExecutor: TaskExecutor,
    private val gitLoaders: Map<GitProperty, GitLoader>,
    private val analysers: Map<AnalyserProperty, Analyser>
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(fixedDelay = 10000)
    fun runAnalysis() {
        pullRequestRepository.findAll()
            .let(withRemovedDuplicates)
            .forEach { pullRequest ->
                taskExecutor.execute(AnalysisRunnable(pullRequest))
                pullRequestRepository.delete(pullRequest)
            }
    }

    private val withRemovedDuplicates: (List<PullRequest>) -> List<PullRequest> = { allPrs ->
        allPrs.minusDuplicates().also { uniquePrs ->
            pullRequestRepository.deleteAll(allPrs.minus(uniquePrs))
        }
    }

    private fun List<PullRequest>.minusDuplicates(): List<PullRequest> {
        return sortedByDescending { it.id }.distinctBy { it.repoFullName to it.creatorName }
    }

    inner class AnalysisRunnable(private val pullRequest: PullRequest) : Runnable {

        override fun run() {
            val gitServiceLoader = gitLoaders.getValue(pullRequest.gitService)
            val analysisService = analysers.getValue(AnalyserProperty.MOSS)
            try {
                logger.loggedAnalysis(pullRequest) {
                    gitServiceLoader.loadFilesFromGit(pullRequest)
                    analysisService.analyse(pullRequest)
                        .also(analysisResultRepository::saveAll)
                        .forEach(::sendStatusCheck)
                }
            } catch (e: Exception) {
                logger.exceptionAtAnalysisOf(pullRequest)
            }
        }

        @Suppress("UNUSED_PARAMETER")
        private fun sendStatusCheck(analysisResult: AnalysisResult) {
            if (pullRequest.gitService == GITHUB) {
                analysisStatusCheckService.send(
                    pullRequest,
                    GithubAnalysisResultData(
                        status = GithubAnalysisStatus.COMPLETED.value,
                        conclusion = GithubAnalysisConclusion.SUCCESS.value
                    )
                )
            }
        }

    }

}
