package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import ru.nikstep.redink.model.entity.BaseFileRecord
import ru.nikstep.redink.model.entity.Repository

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