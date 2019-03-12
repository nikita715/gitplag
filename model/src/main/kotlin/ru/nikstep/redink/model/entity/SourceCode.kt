package ru.nikstep.redink.model.entity

import javax.persistence.*

/**
 * Info class about stored solutions
 */
@Entity
class SourceCode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "p_user")
    val user: String,
    val repo: String,
    val fileName: String,
    val sha: String,
    val sourceBranch: String,
    val targetBranch: String
) {
    constructor(pullRequest: PullRequest, fileName: String) : this(
        user = pullRequest.creatorName,
        repo = pullRequest.mainRepoFullName,
        sourceBranch = pullRequest.sourceBranchName,
        targetBranch = pullRequest.mainBranchName,
        fileName = fileName,
        sha = pullRequest.sourceHeadSha
    )
}