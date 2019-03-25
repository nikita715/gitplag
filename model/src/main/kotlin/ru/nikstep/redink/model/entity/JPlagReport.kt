package ru.nikstep.redink.model.entity

import java.time.LocalDateTime
import javax.persistence.*

/**
 * Report about the result of a jplag analysis that stored locally
 */
@Entity
data class JPlagReport(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(nullable = false)
    val createdAt: LocalDateTime,

    @Column(nullable = false)
    val hash: String
)