package ru.nikstep.redink.git.rest

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository

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
     * Upload all branches of the base [repo] (or only [branch])
     * and store its contents as base files.
     */
    fun cloneRepository(repo: Repository, branch: String? = null)

    /**
     * Get payload from a git service about existing pull requests of the [repo]
     */
    fun findPullRequests(repo: Repository, page: Int): Collection<JsonObject>

}