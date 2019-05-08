package io.gitplag.model.dto

import io.gitplag.model.entity.SolutionFileRecord

/**
 * Solution file dto
 */
class SolutionFileInfoDto(
    val id: Long,
    val name: String,
    val branch: String,
    val student: String
) {
    constructor(solutionFileRecord: SolutionFileRecord) : this(
        solutionFileRecord.id,
        solutionFileRecord.fileName,
        solutionFileRecord.pullRequest.sourceBranchName,
        solutionFileRecord.pullRequest.creatorName
    )
}