package io.gitplag.model.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import javax.persistence.*

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
    @Enumerated(EnumType.STRING)
    val gitService: GitProperty,

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

    @JsonIgnore
    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(mappedBy = "repo", orphanRemoval = true)
    val pullRequests: List<PullRequest> = mutableListOf(),

    @JsonIgnore
    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(mappedBy = "repo", orphanRemoval = true)
    val baseFiles: List<BaseFileRecord> = mutableListOf(),

    @JsonIgnore
    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(mappedBy = "repository", orphanRemoval = true)
    val branches: List<Branch> = mutableListOf(),

    @Column(nullable = false)
    val mossParameters: String = "",

    @Column(nullable = false)
    val jplagParameters: String = "",

    val gitId: Long = -1
) {
    val parameters: String
        get() = when (analyzer) {
            AnalyzerProperty.MOSS -> mossParameters
            AnalyzerProperty.JPLAG -> jplagParameters
        }

    /**
     * Get parameters of the [analyzer]
     */
    fun parametersOfAnalyzer(analyzer: AnalyzerProperty) = when (analyzer) {
        AnalyzerProperty.MOSS -> mossParameters
        AnalyzerProperty.JPLAG -> jplagParameters
    }
}