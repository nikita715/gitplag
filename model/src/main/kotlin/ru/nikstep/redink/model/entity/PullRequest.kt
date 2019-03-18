package ru.nikstep.redink.model.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Received pull request record
 */
@Entity
data class PullRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @ManyToOne
    @JoinColumn(nullable = false)
    val repo: Repository,

    @Column(nullable = false)
    val number: Int,

    @Column(nullable = false)
    val creatorName: String,

    @Column(nullable = false)
    val sourceRepoId: Long,

    @Column(nullable = false)
    val mainRepoId: Long,

    @Column(nullable = false)
    val sourceRepoFullName: String,

    @Column(nullable = false)
    val headSha: String,

    @Column(nullable = false)
    val sourceBranchName: String,

    @Column(nullable = false)
    val mainBranchName: String,

    @Column(nullable = false)
    val date: LocalDateTime
)