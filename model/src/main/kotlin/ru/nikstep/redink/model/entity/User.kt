package ru.nikstep.redink.model.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * User of the system
 */
@Entity
@Table(name = "p_user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,
    val name: String,
    val githubId: Long,
    val installationId: Long
)