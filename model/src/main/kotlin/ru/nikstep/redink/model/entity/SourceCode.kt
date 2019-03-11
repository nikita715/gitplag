package ru.nikstep.redink.model.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * Info class about stored solutions
 */
@Entity
class SourceCode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "p_user")
    val user: String,
    val repo: String,
    val fileName: String,
    val sha: String,
    val branch: String
)