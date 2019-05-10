package io.gitplag.model.repo

import io.gitplag.model.entity.Branch
import io.gitplag.model.entity.Repository
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring data repo of [Branch]
 */
interface BranchRepository : JpaRepository<Branch, Long> {

    /**
     * Find a [Branch] by the [Repository] and by the [name]
     */
    fun findByRepositoryAndName(repository: Repository, name: String): Branch?
}
