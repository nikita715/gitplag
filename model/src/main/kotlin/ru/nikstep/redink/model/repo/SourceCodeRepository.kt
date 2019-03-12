package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import ru.nikstep.redink.model.entity.SourceCode
import ru.nikstep.redink.util.GitProperty

/**
 * Spring data repo of [SourceCode]
 */
interface SourceCodeRepository : JpaRepository<SourceCode, Long> {

    /**
     * Find all solutions for [repo]
     */
    fun findAllByRepo(repo: String): List<SourceCode>

    /**
     * Find all solutions for [repo] and [gitService]
     */
    fun findAllByGitServiceAndRepo(gitService: GitProperty, repo: String): List<SourceCode>

    /**
     * Find all solutions for [repo]
     */
    fun findAllByRepoAndSourceBranch(repo: String, sourceBranch: String): List<SourceCode>

    /**
     * Find all solutions for [repo]
     */
    fun findAllByRepoAndTargetBranch(repo: String, targetBranch: String): List<SourceCode>

    /**
     * Find all solutions for [repo] and [fileName]
     */
    fun findAllByRepoAndFileName(repo: String, fileName: String): List<SourceCode>

    /**
     * Delete [SourceCode] record by its parameters
     */
    @Transactional
    fun deleteByRepoAndUserAndFileNameAndSourceBranch(
        repo: String,
        user: String,
        fileName: String,
        sourceBranch: String
    )
}