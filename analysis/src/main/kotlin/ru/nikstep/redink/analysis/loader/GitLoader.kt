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

}