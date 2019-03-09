package ru.nikstep.redink.model.entity

import ru.nikstep.redink.util.GitProperty
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
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
    val analysisPairLines: List<AnalysisPairLines> = emptyList(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val gitService: GitProperty,

    @Column(nullable = false)
    val student1Sha: String,

    @Column(nullable = false)
    val student2Sha: String
)
