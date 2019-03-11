package ru.nikstep.redink.checks.github

import ru.nikstep.redink.model.entity.PullRequest

/**
 * Github status check manager
 */
interface AnalysisStatusCheckService {

    /**
     * Send status check of the [analysisData] to the [pullRequest]
     */
    fun send(pullRequest: PullRequest, analysisData: GithubAnalysisResultData)

    /**
     * Send in-progress status check to the [pullRequest]
     */
    fun sendInProgressStatus(pullRequest: PullRequest)
}