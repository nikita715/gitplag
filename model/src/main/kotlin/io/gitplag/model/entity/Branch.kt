package io.gitplag.model.entity

import java.time.LocalDateTime
import javax.persistence.*

/**
 * Branch of a [Repository]
 */
@Entity
data class Branch(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(nullable = false)
    val updatedAt: LocalDateTime,

    @ManyToOne
    @JoinColumn(nullable = false)
    val repository: Repository,

    @Column(nullable = false)
    val name: String
)