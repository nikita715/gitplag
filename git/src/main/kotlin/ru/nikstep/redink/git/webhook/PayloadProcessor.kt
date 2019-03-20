package ru.nikstep.redink.git.webhook

import ru.nikstep.redink.model.entity.Repository

/**
 * Service that receives webhook requests about pull requests from git services
 */
interface PayloadProcessor {

    /**
     * Transform payload from a git service
     * and save it as a pull request
     */
    fun downloadSolutionsOfPullRequest(payload: String)

    /**
     * Transform payload from a git service
     * and save it as a pull request
     */
    fun downloadBasesOfRepository(payload: String)

    fun downloadAllPullRequestsOfRepository(repo: Repository)
}