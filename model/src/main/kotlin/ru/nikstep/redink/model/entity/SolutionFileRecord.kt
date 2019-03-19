package ru.nikstep.redink.model.entity

import ru.nikstep.redink.model.data.SourceFileInfo
import javax.persistence.Column
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

    constructor(sourceFileInfo: SourceFileInfo, countOfLines: Int) : this(
        user = sourceFileInfo.creator,
        repo = sourceFileInfo.repo,
        branch = sourceFileInfo.sourceBranchName,
        fileName = sourceFileInfo.fileName,
        sha = sourceFileInfo.headSha,
        countOfLines = countOfLines
    )
}