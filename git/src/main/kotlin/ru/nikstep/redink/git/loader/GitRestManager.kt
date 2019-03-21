package ru.nikstep.redink.git.loader

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository

/**
 * Loader of files from git services
 */
interface GitRestManager {

    /**
     * Upload files that have changed in the pull request
     * to the application repository
     */
    fun clonePullRequest(pullRequest: PullRequest)

    /**
     * Upload files that have changed in the pull request
     * to the application repository
     */
    fun cloneRepository(repo: Repository, branch: String? = null)

    fun findPullRequests(repo: Repository): Collection<JsonObject>

}