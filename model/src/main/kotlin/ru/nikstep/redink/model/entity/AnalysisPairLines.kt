package ru.nikstep.redink.model.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class AnalysisPairLines(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    val from1: Int,
    val to1: Int,

    val from2: Int,
    val to2: Int,

    @ManyToOne
    @JoinColumn
    val analysisPair: AnalysisPair
)