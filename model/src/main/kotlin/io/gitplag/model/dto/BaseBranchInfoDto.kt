package io.gitplag.model.dto

import java.time.LocalDateTime

/**
 * Dto of a base branch
 */
class BaseBranchInfoDto(
    val branch: String,
    val updated: LocalDateTime,
    val files: List<FileInfoDto>
)