package io.gitplag.model.dto

/**
 * Dto for pull requests
 */
class PullRequestDto(
    val id: Long,
    val number: Int,
    val user: String,
    val from: String,
    val to: String
)