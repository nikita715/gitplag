package ru.nikstep.redink.analysis.loader

import ru.nikstep.redink.model.entity.PullRequest

/**
 * Loader of files from git services
 */
interface GitLoader {

    /**
     * Upload files that have changed in the pull request
     * to the application repository
     */
    fun loadFilesFromGit(pullRequest: PullRequest)

    /**
     * Load text of file from git
     */
    fun loadFileText(repoFullName: String, branchName: String, fileName: String): String

    /**
     * Load text of file from git with [secretKey] of the git service
     */
    fun loadFileText(repoFullName: String, branchName: String, fileName: String, secretKey: String): String

}