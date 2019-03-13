package ru.nikstep.redink.model.manager

import org.springframework.transaction.annotation.Transactional
import ru.nikstep.redink.model.entity.JPlagReport
import ru.nikstep.redink.model.repo.JPlagReportRepository
import java.time.LocalDateTime

open class JPlagReportDataManager(private val jPlagReportRepository: JPlagReportRepository) {

    @Transactional(readOnly = true)
    open fun findAllCreatedBefore(date: LocalDateTime) = jPlagReportRepository.findAllCreatedBefore(date)

    @Transactional
    open fun deleteAll(jPlagReports: List<JPlagReport>) = jPlagReportRepository.deleteAll(jPlagReports)

}