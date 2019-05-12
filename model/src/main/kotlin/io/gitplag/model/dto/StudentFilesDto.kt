package io.gitplag.model.dto

import java.time.LocalDateTime

/**
 * Dto of a student solution files
 */
class StudentFilesDto(
    val student: String,
    val updated: LocalDateTime,
    val files: Collection<FileInfoDto>
)