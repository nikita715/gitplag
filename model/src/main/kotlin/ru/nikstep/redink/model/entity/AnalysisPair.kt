package ru.nikstep.redink.model.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

/**
 * Result of the plagiarism analysis of two files
 */
@Entity
class AnalysisPair(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(nullable = false)
    val student1: String = "",

    @Column(nullable = false)
    val student2: String = "",

    @Column(nullable = false)
    val lines: Int = 0,

    @Column(nullable = false)
    val percentage: Int = 0,

    @Column(nullable = false)
    val repo: String,

    @Column(nullable = false)
    val fileName: String,

    @OneToMany(mappedBy = "analysisPair", orphanRemoval = true, fetch = FetchType.EAGER)
    val analysisPairLines: List<AnalysisPairLines> = emptyList()
)
