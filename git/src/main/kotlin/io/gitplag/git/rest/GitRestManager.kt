package io.gitplag.git.rest

import com.beust.klaxon.JsonObject
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository

/**
 * The service that interacts with git services apis
 */
interface GitRestManager {

    /**
     * Upload branch of the repo from which the [pullRequest] was created
     * and store its contents as solutions
     */
    fun clonePullRequest(pullRequest: PullRequest)

    /**
     * Upload all branches of the base [repo] (or only the [branch])
     * and store the contents as base files.
     */
    fun cloneRepository(repo: Repository, branch: String? = null)

    /**
     * Get payload from a git service about existing pull requests of the [repo]
     */
    fun findPullRequests(repo: Repository, page: Int): Collection<JsonObject>

    /**
     * Request a list of branch names
     */
    fun findBranchesOfRepo(repo: Repository): List<String>

    /**
     * Request a branch payload
     */
    fun getBranchOfRepo(repo: Repository, name: String): JsonObject

}