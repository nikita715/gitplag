package io.gitplag.model.repo

import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.GitProperty
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * Spring data repo of [Repository]
 */
interface RepositoryRepository : JpaRepository<Repository, Long> {

    /**
     * Find a [Repository] by [gitService] and [name]
     */
    fun findByGitServiceAndName(gitService: GitProperty, name: String): Repository?

    /**
     * Find repositories with periodic_analysis = true that must be analyzed now
     */
    @Query(
        nativeQuery = true, value = """
            select * from repository
            where repository.periodic_analysis = true
            and extract(epoch from now() - (select execution_date
            from analysis
            where analysis.repository_id = repository.id
            order by id desc
            limit 1) at time zone 'MSK') / 60 > repository.periodic_analysis_delay"""
    )
    fun findRequiredToAnalyze(): List<Repository>

    /**
     * Delete repository by [gitService] and [name]
     */
    fun deleteByGitServiceAndName(gitService: GitProperty, name: String)
}
