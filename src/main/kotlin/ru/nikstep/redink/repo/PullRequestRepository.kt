package ru.nikstep.redink.repo

import org.springframework.data.jpa.repository.JpaRepository
import ru.nikstep.redink.entity.PullRequest

interface PullRequestRepository : JpaRepository<PullRequest, Long>