package io.gitplag.model.entity

import com.fasterxml.jackson.annotation.JsonManagedReference
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.Language
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
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
    val repository: Repository,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val analyzer: AnalyzerProperty,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val language: Language,

    @Column(nullable = false)
    val branch: String,

    @Column(nullable = false)
    val executionDate: LocalDateTime,

    @Column(nullable = false)
    val resultLink: String,

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