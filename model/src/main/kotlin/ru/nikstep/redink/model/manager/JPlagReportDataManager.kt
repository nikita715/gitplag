package ru.nikstep.redink.model.manager

import org.springframework.transaction.annotation.Transactional
import ru.nikstep.redink.model.entity.JPlagReport
import ru.nikstep.redink.model.repo.JPlagReportRepository
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