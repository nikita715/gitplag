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
    fun loadFilesOfCommit(pullRequest: PullRequest)

    /**
     * Upload files that have changed in the pull request
     * to the application repository
     */
    fun loadFilesOfPullRequest(pullRequest: PullRequest)

    /**
     * Upload files that have changed in the pull request
     * to the application repository
     */
    fun loadRepositoryAndPullRequestFiles(repo: Repository)

    /**
     * Load text of file from git
     */
    fun loadFileText(repoFullName: String, branchName: String, fileName: String): String

}