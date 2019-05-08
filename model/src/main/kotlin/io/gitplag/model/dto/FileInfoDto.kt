package io.gitplag.model.dto

/**
 * Dto for base and solution files
 */
data class FileInfoDto(val bases: List<BaseFileInfoDto>, val solutions: List<SolutionFileInfoDto>)