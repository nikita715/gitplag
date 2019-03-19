package ru.nikstep.redink.model.entity

import javax.persistence.*

/**
 * Info class about stored solutions
 */
@Entity
class SolutionFileRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "p_user")
    val user: String,

    @ManyToOne
    @JoinColumn(nullable = false)
    val repo: Repository,

    val fileName: String,
    val sha: String,
    val countOfLines: Int,
    val branch: String
) {
    constructor(pullRequest: PullRequest, fileName: String, countOfLines: Int) : this(
        user = pullRequest.creatorName,
        repo = pullRequest.repo,
        branch = pullRequest.sourceBranchName,
        fileName = fileName,
        sha = pullRequest.headSha,
        countOfLines = countOfLines
    )
}