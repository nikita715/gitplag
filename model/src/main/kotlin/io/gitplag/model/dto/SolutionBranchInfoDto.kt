package io.gitplag.model.dto

class SolutionBranchInfoDto(
    val sourceBranch: String,
    val students: Collection<StudentFilesDto>
)