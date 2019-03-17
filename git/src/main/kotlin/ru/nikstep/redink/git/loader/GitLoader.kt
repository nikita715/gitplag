package ru.nikstep.redink.git.loader

import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.SourceCode

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
    fun loadFilesOfRepository(repo: Repository): List<SourceCode>

    /**
     * Load text of file from git
     */
    fun loadFileText(repoFullName: String, branchName: String, fileName: String): String

    /**
     * Load text of file from git with [secretKey] of the git service
     */
    fun loadFileText(repoFullName: String, branchName: String, fileName: String, secretKey: String): String

}