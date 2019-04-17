package io.gitplag.model.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * Git repository
 */
@Entity
@Table(name = "repository")
data class Repository(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long = -1,

    @Column(name = "pattern")
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "repository_pattern", joinColumns = [JoinColumn(name = "repository")])
    val filePatterns: Collection<String> = listOf(".+\\.java"),

    val name: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val analyzer: AnalyzerProperty = AnalyzerProperty.MOSS,

    @Column(nullable = false)
    val periodicAnalysis: Boolean = false,

    @Column(nullable = false)
    val periodicAnalysisDelay: Int = 1,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val gitService: GitProperty,

    @Column(nullable = false)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "analysed_branch", joinColumns = [JoinColumn(name = "repository")])
    val branches: List<String> = listOf("master"),

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val language: Language = Language.JAVA,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val analysisMode: AnalysisMode = AnalysisMode.PAIRS,

    @Column(nullable = false)
    val autoCloningEnabled: Boolean = true,

    @JsonIgnore
    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(mappedBy = "repository", orphanRemoval = true)
    val analyzes: List<Analysis> = mutableListOf(),

    @Column(nullable = false)
    val mossParameters: String = "",

    @Column(nullable = false)
    val jplagParameters: String = "",

    val gitId: Long = -1
)