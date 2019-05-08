package io.gitplag.model.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Info class about stored solutions
 */
@Entity
data class SolutionFileRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @ManyToOne
    @JsonIgnore
    @JoinColumn(nullable = false)
    val pullRequest: PullRequest,

    val fileName: String,

    val countOfLines: Int
)
