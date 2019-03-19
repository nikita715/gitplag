package ru.nikstep.redink.git.loader

import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository

/**
 * Loader of files from git services
 */
interface GitLoader {

    /**
     * Upload files that have changed in the pull request
     * to the application repository
     */
    @Deprecated("Migrated to repo cloning")
    fun loadFilesOfCommit(pullRequest: PullRequest)

    /**
     * Upload files that have changed in the pull request
     * to the application repository
     */
    fun clonePullRequest(pullRequest: PullRequest)

    /**
     * Upload files that have changed in the pull request
     * to the application repository
     */
    fun cloneRepositoryAndPullRequests(repo: Repository)

    /**
     * Load text of file from git
     */
    @Deprecated("Migrated to repo cloning")
    fun loadFileText(repoFullName: String, branchName: String, fileName: String): String

}