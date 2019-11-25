package io.gitplag.git.payload

import io.gitplag.model.dto.InputRepositoryDto
import io.gitplag.model.entity.Repository

/**
 * Service for managing of interactions with git services
 */
interface GitManager {

    /**
     * Transform payload from a git service
     * and save its contents as a pull request and solution files
     */
    fun downloadSolutionsOfPullRequest(payload: String)

    /**
     * Transform payload from a git service
     * and save its contents as solution files
     */
    fun downloadBasesOfRepository(payload: String)

    /**
     * Requests base branches and pull requests payloads and stores all of them
     */
    fun downloadAllPullRequestsOfRepository(repo: Repository)

    /**
     * Create a repo by the [dto]
     */
    fun createRepo(dto: InputRepositoryDto): Repository?
}