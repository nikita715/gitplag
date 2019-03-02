package ru.nikstep.redink.analysis.loader

import ru.nikstep.redink.model.entity.PullRequest

interface GitLoader {

    fun loadFilesFromGit(pullRequest: PullRequest)

}