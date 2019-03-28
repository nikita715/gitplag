package ru.nikstep.redink.model.entity

import javax.persistence.*

/**
 * Info class about stored solutions
 */
@Entity
data class SolutionFileRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @ManyToOne
    @JoinColumn(nullable = false)
    val pullRequest: PullRequest,

    val fileName: String,

    val countOfLines: Int
)
