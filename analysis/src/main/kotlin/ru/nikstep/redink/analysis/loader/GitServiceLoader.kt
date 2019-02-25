package ru.nikstep.redink.analysis.loader

import ru.nikstep.redink.model.entity.PullRequest

interface GitServiceLoader {

    fun loadFilesFromGit(pullRequest: PullRequest)

}