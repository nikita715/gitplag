package ru.nikstep.redink.analysis

import mu.KotlinLogging
import org.springframework.context.ApplicationListener
import ru.nikstep.redink.analysis.loader.GitLoader
import ru.nikstep.redink.git.PullRequestEvent
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty

class PullRequestListener(
    private val gitLoaders: Map<GitProperty, GitLoader>
) : ApplicationListener<PullRequestEvent> {
    private val logger = KotlinLogging.logger {}

    override fun onApplicationEvent(event: PullRequestEvent) = loadChangedFiles(event.pullRequest)

    private fun loadChangedFiles(pullRequest: PullRequest) {
        val gitServiceLoader = gitLoaders.getValue(pullRequest.gitService)
        gitServiceLoader.loadFilesFromGit(pullRequest)
    }
}