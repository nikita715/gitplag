package ru.nikstep.redink.model.entity

import ru.nikstep.redink.model.data.SourceFileInfo
import ru.nikstep.redink.util.GitProperty
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

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
    val countOfLines: Int,
    val sourceBranch: String,
    val targetBranch: String,

    @Enumerated(EnumType.STRING)
    val gitService: GitProperty
) {
    constructor(pullRequest: PullRequest, fileName: String, countOfLines: Int) : this(
        user = pullRequest.creatorName,
        repo = pullRequest.mainRepoFullName,
        sourceBranch = pullRequest.sourceBranchName,
        targetBranch = pullRequest.mainBranchName,
        fileName = fileName,
        sha = pullRequest.headSha,
        countOfLines = countOfLines,
        gitService = pullRequest.gitService
    )

    constructor(sourceFileInfo: SourceFileInfo, countOfLines: Int) : this(
        user = sourceFileInfo.creator,
        repo = sourceFileInfo.mainRepoFullName,
        sourceBranch = sourceFileInfo.sourceBranchName,
        targetBranch = sourceFileInfo.mainBranchName,
        fileName = sourceFileInfo.fileName,
        sha = sourceFileInfo.headSha,
        countOfLines = countOfLines,
        gitService = sourceFileInfo.gitService
    )
}