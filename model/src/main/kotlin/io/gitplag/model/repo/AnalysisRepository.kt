package io.gitplag.model.repo

import io.gitplag.model.entity.Analysis
import io.gitplag.model.entity.Repository
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring data repo of [Analysis]
 */
interface AnalysisRepository : JpaRepository<Analysis, Long> {

    /**
     * Finds the last analysis of the [repository]
     */
    fun findFirstByRepositoryOrderByExecutionDateDesc(repository: Repository): Analysis?
}