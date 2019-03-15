package ru.nikstep.redink.core.analysis

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.manager.JPlagReportDataManager
import ru.nikstep.redink.model.repo.RepositoryRepository
import java.io.File
import java.time.LocalDateTime

/**
 * Scheduler of periodic analysis tasks
 */
@Component
class AnalysisScheduler(
    private val analysisAsyncRunner: AnalysisAsyncRunner,
    private val repositoryRepository: RepositoryRepository,
    private val jPlagReportDataManager: JPlagReportDataManager,
    @Value("\${redink.jplagResultDir}") private val jplagResultDir: String
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
        }.forEach { analysisAsyncRunner.run(it) }
        logger.info { "Core: end analyzes" }
    }

    /**
     * Delete outdated jplag reports
     */
    @Scheduled(cron = "0 0 0 * * *")
    fun deleteOutdatedReports() {
        logger.info { "Core: look for outdated jplag reports" }
        jPlagReportDataManager.findAllCreatedBefore(LocalDateTime.now().minusDays(14)).onEach {
            File(jplagResultDir + it.hash).deleteRecursively()
        }.also(jPlagReportDataManager::deleteAll)
        logger.info { "Core: outdated jplag reports were deleted" }
    }
}