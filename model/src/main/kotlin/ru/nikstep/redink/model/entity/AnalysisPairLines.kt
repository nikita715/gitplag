package ru.nikstep.redink.model.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Lines (of two files with the same name) suspected of plagiarism
 */
@Entity
class AnalysisPairLines(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(nullable = false)
    val from1: Int,

    @Column(nullable = false)
    val to1: Int,

    @Column(nullable = false)
    val from2: Int,

    @Column(nullable = false)
    val to2: Int,

    @ManyToOne
    @JoinColumn(nullable = false)
    val analysisPair: AnalysisPair
)