package ru.nikstep.redink.model.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class AnalysisTask(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(nullable = false)
    val studentName: String,

    @Column(nullable = false)
    val repoName: String,

    @Column(nullable = false)
    val fileName: String

)