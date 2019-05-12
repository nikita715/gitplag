package io.gitplag.model.dto

/**
 * Dto for base and solution files
 */
data class RepositoryFilesInfoDto(
    val bases: Collection<BaseBranchInfoDto>,
    val solutions: Collection<SolutionBranchInfoDto>
)