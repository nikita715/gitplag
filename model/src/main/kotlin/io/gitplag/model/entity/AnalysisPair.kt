package io.gitplag.model.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import java.time.LocalDateTime
import java.util.Objects
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

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
    val percentage: Int = 0,

    @Column(nullable = false)
    val minPercentage: Int = 0,

    @Column(nullable = false)
    val maxPercentage: Int = 0,

    @field:JsonManagedReference
    @get:JsonManagedReference
    @ElementCollection(fetch = FetchType.LAZY)
    @OneToMany(mappedBy = "analysisPair", orphanRemoval = true)
    val analysisPairLines: List<AnalysisPairLines> = mutableListOf(),

    @Column(nullable = false)
    val sha1: String,

    @Column(nullable = false)
    val sha2: String,

    @Column(nullable = false)
    val createdAt1: LocalDateTime,

    @Column(nullable = false)
    val createdAt2: LocalDateTime,

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
