package ru.nikstep.redink.model.entity

import javax.persistence.*

/**
 * Info class about stored base files
 */
@Entity
data class BaseFileRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @ManyToOne
    @JoinColumn(nullable = false)
    val repo: Repository,

    @Column(nullable = false)
    val fileName: String,

    @Column(nullable = false)
    val branch: String
)