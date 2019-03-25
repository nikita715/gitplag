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
     * Find all solutions by [repo]
     */
    @Query(
        "from SolutionFileRecord sf WHERE sf.pullRequest in " +
                "(select pr from PullRequest pr where pr.repo = ?1)"
    )
    fun findAllByRepo(repo: Repository): List<SolutionFileRecord>

    /**
     * Delete all [SolutionFileRecord]s of the [pullRequest]
     */
    @Transactional
    fun deleteAllByPullRequest(pullRequest: PullRequest)

    /**
     * Find all [SolutionFileRecord]s of the [pullRequest]
     */
    fun findAllByPullRequest(pullRequest: PullRequest): List<SolutionFileRecord>

}