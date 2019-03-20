package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository

/**
 * Spring data repo of [PullRequest]
 */
interface PullRequestRepository : JpaRepository<PullRequest, Long> {

    /**
     * Finds the last record about the [PullRequest] by its [number]
     */
    fun findFirstBySourceRepoFullNameAndNumberOrderByIdDesc(repoFullName: String, number: Int): PullRequest

    fun findByRepoAndNumber(repo: Repository, number: Int): PullRequest?
}