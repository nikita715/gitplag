package ru.nikstep.redink.core.analysis

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.AnalysisMode

/**
 * Scheduler of [AnalysisMode.PERIODIC] analysis tasks
 */
open class AnalysisScheduler(
    private val analysisRunner: AnalysisRunner,
    private val repositoryRepository: RepositoryRepository
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Find repositories required to analyse and run analyzes
     */
    @Scheduled(cron = "0 * * * * *")
    fun initiateAnalysis() {
        logger.info { "Core: look for periodic analyzes" }
        val requiredToAnalyse = repositoryRepository.findRequiredToAnalyse()
        logger.info { "Core: found ${requiredToAnalyse.size} required analyzes" }
        requiredToAnalyse.flatMap { repository ->
            repository.branches.map { branch ->
                AnalysisSettings(repository, branch)
            }
        }.forEach { initiateAsync(it) }
        logger.info { "Core: end analyzes" }
    }

    /**
     * Initiate analysis of the [repository] async
     */
    @Async("analysisThreadPoolTaskExecutor")
    open fun initiateAsync(settings: AnalysisSettings) {
        try {
            logger.loggedAnalysis(settings) {
                analysisRunner.run(settings)
            }
        } catch (e: Exception) {
            logger.exceptionAtAnalysisOf(e, settings)
        }
    }
}