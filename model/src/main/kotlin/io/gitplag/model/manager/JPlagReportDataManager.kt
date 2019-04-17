package io.gitplag.model.manager

import io.gitplag.model.entity.JPlagReport
import io.gitplag.model.repo.JPlagReportRepository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Data manager of [JPlagReport]
 */
@Transactional
class JPlagReportDataManager(private val jPlagReportRepository: JPlagReportRepository) {

    /**
     * Find all outdated [JPlagReport]s
     */
    @Transactional(readOnly = true)
    fun findAllCreatedBefore(date: LocalDateTime) = jPlagReportRepository.findAllCreatedBefore(date)

    /**
     * Delete all [jPlagReports]
     */
    @Transactional
    fun deleteAll(jPlagReports: List<JPlagReport>) = jPlagReportRepository.deleteAll(jPlagReports)

}