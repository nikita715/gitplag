package ru.nikstep.redink.model.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class Analysis(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @ManyToOne
    @JoinColumn
    val repository: Repository,

    @Column(nullable = false)
    val executionDate: LocalDateTime,

    @OneToMany(mappedBy = "analysis", orphanRemoval = true)
    val analysisPairs: Collection<AnalysisPair> = emptyList()
)