package io.gitplag.model.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import java.time.LocalDateTime
import javax.persistence.*

/**
 * Received pull request record
 */
@Entity
data class PullRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @ManyToOne
    @JoinColumn(nullable = false)
    val repo: Repository,

    @Column(nullable = false)
    val number: Int,

    @Column(nullable = false)
    val creatorName: String,

    @Column(nullable = false)
    val sourceRepoFullName: String,

    @Column(nullable = false)
    val headSha: String,

    @Column(nullable = false)
    val sourceBranchName: String,

    @Column(nullable = false)
    val mainBranchName: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime,

    @Column(nullable = false)
    val updatedAt: LocalDateTime,

    @JsonIgnore
    @LazyCollection(LazyCollectionOption.TRUE)
    @OneToMany(mappedBy = "pullRequest", orphanRemoval = true)
    val pullRequests: List<SolutionFileRecord> = mutableListOf()
)