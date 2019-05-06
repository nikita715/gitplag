package io.gitplag.model.dto

/**
 * Dto with info about a downloaded file
 */
class LocalFileDto(
    val id: Long,
    val branch: String?,
    val fileName: String?,
    val student: String?
)