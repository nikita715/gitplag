package io.gitplag.model.repo

import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.GitProperty
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring data repo of [Repository]
 */
interface RepositoryRepository : JpaRepository<Repository, Long> {

    /**
     * Find a [Repository] by [gitService] and [name]
     */
    fun findByGitServiceAndName(gitService: GitProperty, name: String): Repository?

    /**
     * Delete repository by [gitService] and [name]
     */
    fun deleteByGitServiceAndName(gitService: GitProperty, name: String)
}
