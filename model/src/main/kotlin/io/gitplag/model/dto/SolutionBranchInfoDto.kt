package io.gitplag.model.dto

/**
 * Dto of a source solution branch
 */
class SolutionBranchInfoDto(
    val sourceBranch: String,
    val students: Collection<StudentFilesDto>
)