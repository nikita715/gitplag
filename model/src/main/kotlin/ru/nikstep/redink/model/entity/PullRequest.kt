package ru.nikstep.redink.model.entity

import ru.nikstep.redink.util.GitProperty
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * Received pull request record
 */
@Entity
data class PullRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(nullable = false)
    val number: Int,

    @Column(nullable = false)
    val secretKey: String = "",

    @Column(nullable = false)
    val creatorName: String,

    @Column(nullable = false)
    val repoId: Long,

    @Column(nullable = false)
    val repoFullName: String,

    @Column(nullable = false)
    val headSha: String,

    @Column(nullable = false)
    val branchName: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val gitService: GitProperty,

    @Column(nullable = false)
    val date: LocalDateTime
)