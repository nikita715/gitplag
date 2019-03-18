package ru.nikstep.redink.model.entity

import ru.nikstep.redink.model.data.SourceFileInfo
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

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
    val repo: Repository,
    val fileName: String,
    val sha: String,
    val countOfLines: Int,
    val sourceBranch: String,
    val targetBranch: String
) {
    constructor(pullRequest: PullRequest, fileName: String, countOfLines: Int) : this(
        user = pullRequest.creatorName,
        repo = pullRequest.repo,
        sourceBranch = pullRequest.sourceBranchName,
        targetBranch = pullRequest.mainBranchName,
        fileName = fileName,
        sha = pullRequest.headSha,
        countOfLines = countOfLines
    )

    constructor(sourceFileInfo: SourceFileInfo, countOfLines: Int) : this(
        user = sourceFileInfo.creator,
        repo = sourceFileInfo.repo,
        sourceBranch = sourceFileInfo.sourceBranchName,
        targetBranch = sourceFileInfo.mainBranchName,
        fileName = sourceFileInfo.fileName,
        sha = sourceFileInfo.headSha,
        countOfLines = countOfLines
    )
}