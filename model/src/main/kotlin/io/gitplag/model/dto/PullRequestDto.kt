package io.gitplag.model.dto

import io.gitplag.model.entity.PullRequest

/**
 * Dto for pull requests
 */
class PullRequestDto(
    val id: Long,
    val number: Int,
    val user: String,
    val from: String,
    val to: String
) {
    constructor(pullRequest: PullRequest) : this(
        pullRequest.id,
        pullRequest.number,
        pullRequest.creatorName,
        pullRequest.sourceBranchName,
        pullRequest.mainBranchName
    )
}