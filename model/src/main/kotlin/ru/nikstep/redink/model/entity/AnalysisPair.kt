package ru.nikstep.redink.model.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class AnalysisPair(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    val student1: String = "",

    val student2: String = "",

    val lines: Int = 0,

    val percentage: Int = 0,

    @OneToMany(mappedBy = "analysisPair")
    val analysisPairLines: List<AnalysisPairLines> = emptyList()
)
