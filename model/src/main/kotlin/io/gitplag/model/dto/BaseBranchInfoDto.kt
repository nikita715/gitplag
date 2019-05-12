package io.gitplag.model.dto

import java.time.LocalDateTime

class BaseBranchInfoDto(
    val branch: String,
    val updated: LocalDateTime,
    val files: List<FileInfoDto>
)