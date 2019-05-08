package io.gitplag.model.repo

import io.gitplag.model.entity.Branch
import io.gitplag.model.entity.Repository
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring data repo of [Branch]
 */
interface BranchRepository : JpaRepository<Branch, Long> {
    fun findByRepositoryAndName(repository: Repository, name: String): Branch?
}
