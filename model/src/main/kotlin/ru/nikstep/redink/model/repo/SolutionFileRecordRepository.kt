package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.SolutionFileRecord

/**
 * Spring data repo of [SolutionFileRecord]
 */
interface SolutionFileRecordRepository : JpaRepository<SolutionFileRecord, Long> {

    /**
     * Find all solutions for [repo] and [gitService]
     */
    @Query(
        "from SolutionFileRecord sf WHERE sf.pullRequest in " +
                "(select pr from PullRequest pr where pr.repo = ?1)"
    )
    fun findAllByRepo(repo: Repository): List<SolutionFileRecord>

    /**
     * Find all solutions for [repo]
     */
    @Query(
        "from SolutionFileRecord sf WHERE sf.pullRequest in " +
                "(select pr from PullRequest pr where pr.repo = ?1 and pr.sourceBranchName = ?2)"
    )
    fun findAllByRepoAndBranch(repo: Repository, branch: String): List<SolutionFileRecord>

    @Transactional
    fun deleteAllByPullRequest(pullRequest: PullRequest)

    fun findAllByPullRequest(pullRequest: PullRequest): List<SolutionFileRecord>

}