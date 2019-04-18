package io.gitplag.model.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

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