package io.gitplag.git.payload

import io.gitplag.model.entity.Repository

/**
 * Service that receives webhook requests about pull requests from git services
 */
interface PayloadProcessor {

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
     * Requests pull requests payload and stores all of them
     */
    fun downloadAllPullRequestsOfRepository(repo: Repository)
}