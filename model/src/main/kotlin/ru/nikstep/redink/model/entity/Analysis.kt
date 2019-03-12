package ru.nikstep.redink.model.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.Language
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

/**
 * Result of the plagiarism analysis
 */
@Entity
data class Analysis(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = -1,

    @ManyToOne
    @JoinColumn
    @field:JsonBackReference
    @get:JsonBackReference
    val repository: Repository,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val analyser: AnalyserProperty,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val language: Language,

    @Column(nullable = false)
    val branch: String,

    @Column(nullable = false)
    val executionDate: LocalDateTime,

    @field:JsonManagedReference
    @get:JsonManagedReference
    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(mappedBy = "analysis", orphanRemoval = true)
    val analysisPairs: List<AnalysisPair> = mutableListOf()
) {

    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?) = this::class.isInstance(other)
}