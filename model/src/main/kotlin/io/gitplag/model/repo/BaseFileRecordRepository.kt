package io.gitplag.model.repo

import io.gitplag.model.entity.BaseFileRecord
import io.gitplag.model.entity.Repository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

/**
 * Spring data repo of [BaseFileRecord]
 */
interface BaseFileRecordRepository : JpaRepository<BaseFileRecord, Long> {

    /**
     * Find all [BaseFileRecord]s by the [repo] and by the [branch]
     */
    fun findAllByRepoAndBranch(repo: Repository, branch: String): List<BaseFileRecord>

    /**
     * Find all [BaseFileRecord]s by the [repo]
     */
    fun findAllByRepo(repo: Repository): List<BaseFileRecord>

    /**
     * Delete all [BaseFileRecord]s by the [repo] and by the [branch]
     */
    @Transactional
    fun deleteAllByRepoAndBranch(repo: Repository, branch: String)

}