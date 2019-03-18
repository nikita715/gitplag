package ru.nikstep.redink.model.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

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