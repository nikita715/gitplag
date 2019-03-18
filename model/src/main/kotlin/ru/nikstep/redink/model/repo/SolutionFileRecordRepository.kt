package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import ru.nikstep.redink.model.entity.SolutionFileRecord
import ru.nikstep.redink.util.GitProperty

/**
 * Spring data repo of [SolutionFileRecord]
 */
interface SolutionFileRecordRepository : JpaRepository<SolutionFileRecord, Long> {

    /**
     * Find all solutions for [repo] and [gitService]
     */
    fun findAllByGitServiceAndRepo(gitService: GitProperty, repo: String): List<SolutionFileRecord>

    /**
     * Find all solutions for [repo]
     */
    fun findAllByRepoAndSourceBranch(repo: String, sourceBranch: String): List<SolutionFileRecord>

    /**
     * Delete [SolutionFileRecord] record by its parameters
     */
    @Transactional
    fun deleteByRepoAndUserAndFileNameAndSourceBranch(
        repo: String,
        user: String,
        fileName: String,
        sourceBranch: String
    )
}