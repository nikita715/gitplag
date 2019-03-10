package ru.nikstep.redink.core

import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import ru.nikstep.redink.analysis.AnalysisManager
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.repo.RepositoryRepository

open class AnalysisScheduler(
    private val analysisManager: AnalysisManager,
    private val repositoryRepository: RepositoryRepository
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(cron = "0 * * * * *")
    fun initiateAnalysis() {
        logger.info { "Core: look for periodic analyzes" }
        val requiredToAnalyse = repositoryRepository.findRequiredToAnalyse()
        logger.info { "Core: found ${requiredToAnalyse.size} required analyzes" }
        requiredToAnalyse.forEach { initiateAsync(it) }
        logger.info { "Core: end analyzes" }
    }

    @Async("analysisThreadPoolTaskExecutor")
    open fun initiateAsync(repository: Repository) {
        analysisManager.initiateAnalysis(repository)
    }

}