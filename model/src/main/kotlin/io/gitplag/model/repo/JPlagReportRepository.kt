package io.gitplag.model.repo

import io.gitplag.model.entity.JPlagReport
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

/**
 * Spring data repo of [JPlagReport]
 */
interface JPlagReportRepository : JpaRepository<JPlagReport, Long> {

    /**
     * Find all outdated [JPlagReport]s
     */
    @Query("from JPlagReport r where r.createdAt < ?1")
    fun findAllCreatedBefore(date: LocalDateTime): List<JPlagReport>

}