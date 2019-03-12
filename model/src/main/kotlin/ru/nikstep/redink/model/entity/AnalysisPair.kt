package ru.nikstep.redink.model.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import ru.nikstep.redink.util.GitProperty
import java.util.*
import javax.persistence.*

/**
 * Result of the plagiarism analysis of two files
 */
@Entity
data class AnalysisPair(
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

    @field:JsonManagedReference
    @get:JsonManagedReference
    @ElementCollection(fetch = FetchType.LAZY)
    @OneToMany(mappedBy = "analysisPair", orphanRemoval = true)
    val analysisPairLines: List<AnalysisPairLines> = mutableListOf(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val gitService: GitProperty,

    @Column(nullable = false)
    val student1Sha: String,

    @Column(nullable = false)
    val student2Sha: String,

    @ManyToOne
    @field:JsonBackReference
    @get:JsonBackReference
    @JoinColumn(nullable = false)
    var analysis: Analysis
) {
    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?) = this::class.isInstance(other)
}
