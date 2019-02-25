package ru.nikstep.redink.model.entity

import javax.persistence.*

@Entity
class SourceCode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "p_user")
    val user: String,
    val repo: String,
    val fileName: String
)