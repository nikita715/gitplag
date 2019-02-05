package ru.nikstep.redink.model.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.model.entity.PullRequest

interface PullRequestRepository : JpaRepository<PullRequest, Long> {
    fun findAllByAnalysedIsFalse(): List<PullRequest>
    fun countAllByAnalysedIsFalse(): Int
}