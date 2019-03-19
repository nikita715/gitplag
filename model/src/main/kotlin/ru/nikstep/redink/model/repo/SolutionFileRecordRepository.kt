package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.SolutionFileRecord

/**
 * Spring data repo of [SolutionFileRecord]
 */
interface SolutionFileRecordRepository : JpaRepository<SolutionFileRecord, Long> {

    /**
     * Find all solutions for [repo] and [gitService]
     */
    fun findAllByRepo(repo: Repository): List<SolutionFileRecord>

    /**
     * Find all solutions for [repo]
     */
    fun findAllByRepoAndBranch(repo: Repository, branch: String): List<SolutionFileRecord>

    /**
     * Delete [SolutionFileRecord] record by its parameters
     */
    @Transactional
    fun deleteByRepoAndUserAndFileNameAndBranch(
        repo: Repository,
        user: String,
        fileName: String,
        branch: String
    )
}