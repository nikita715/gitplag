package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.Analysis
import ru.nikstep.redink.model.entity.Repository

/**
 * Spring data repo of [Analysis]
 */
interface AnalysisRepository : JpaRepository<Analysis, Long> {

    /**
     * Finds the last analysis of the [repository]
     */
    fun findFirstByRepositoryOrderByExecutionDateDesc(repository: Repository): Analysis?
}