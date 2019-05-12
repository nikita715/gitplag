package io.gitplag.model.dto

import java.time.LocalDateTime

class StudentFilesDto(
    val student: String,
    val updated: LocalDateTime,
    val files: Collection<FileInfoDto>
)