package ru.nikstep.redink.model.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.AnalysisMode
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language
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
    val analyseWithLines: Boolean = false,

    @Column(nullable = false)
    val analysisDelay: Int = 1,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val gitService: GitProperty,

    @Column(nullable = false)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "analysed_branch", joinColumns = [JoinColumn(name = "repository")])
    val branches: List<String> = emptyList(),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val language: Language,

    @JsonIgnore
    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(mappedBy = "repository", orphanRemoval = true)
    val analyzes: List<Analysis> = mutableListOf()
)