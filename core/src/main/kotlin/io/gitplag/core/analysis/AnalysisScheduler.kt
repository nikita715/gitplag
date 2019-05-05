package io.gitplag.core.analysis

import io.gitplag.model.manager.JPlagReportDataManager
import io.gitplag.model.manager.RepositoryDataManager
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.time.LocalDateTime

/**
 * Scheduler of periodic analysis tasks
 */
@Component
class AnalysisScheduler(
    private val analysisAsyncRunner: AnalysisAsyncRunner,
    private val repositoryDataManager: RepositoryDataManager,
    private val jPlagReportDataManager: JPlagReportDataManager,
    @Value("\${gitplag.jplagResultDir}") private val jplagResultDir: String
) {
    private val logger = KotlinLogging.logger {}

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