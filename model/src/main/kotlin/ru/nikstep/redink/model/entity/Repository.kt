package ru.nikstep.redink.model.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import ru.nikstep.redink.util.*
import javax.persistence.*

/**
 * Git repository
 */
@Entity
@Table(name = "repository")
class Repository(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = -1,

    @ManyToOne
    @JoinColumn(name = "owner")
    val owner: User,

    @Column(name = "pattern")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "repository_pattern", joinColumns = [JoinColumn(name = "repository")])
    val filePatterns: Collection<String> = emptyList(),

    val name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val analyser: AnalyserProperty,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val analysisMode: AnalysisMode,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val analysisBranchMode: AnalysisBranchMode,

    @Column(nullable = false)
    val analysisDelay: Int = 1,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val gitService: GitProperty,

    @Column(nullable = false)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "analysed_branch", joinColumns = [JoinColumn(name = "repository")])
    val branches: List<String> = emptyList(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val language: Language,

    @field:JsonIgnore
    @get:JsonIgnore
    @field:JsonManagedReference
    @get:JsonManagedReference
    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(mappedBy = "repository", orphanRemoval = true)
    val analyzes: List<Analysis> = mutableListOf()
)