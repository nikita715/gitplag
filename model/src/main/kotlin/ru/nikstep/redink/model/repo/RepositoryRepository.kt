package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.GitProperty

/**
 * Spring data repo of [Repository]
 */
interface RepositoryRepository : JpaRepository<Repository, Long> {

    /**
     * Find a [Repository] by [gitService] and [name]
     */
    fun findByGitServiceAndName(gitService: GitProperty, name: String): Repository

    @Query(
        nativeQuery = true, value = """select * from repository
where repository.analysis_mode = 'PERIODIC' and extract(epoch from now() - (select execution_date
                                  from analysis
                                  where analysis.repository_id = 5
                                  order by id desc
                                  limit 1) at time zone 'MSK') / 60 > repository.analysis_delay;"""
    )
    fun findRequiredToAnalyse(): List<Repository>
}
