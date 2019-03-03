package ru.nikstep.redink.checks.github

import ru.nikstep.redink.model.entity.PullRequest

interface AnalysisStatusCheckService {

    fun send(pullRequest: PullRequest, analysisData: GithubAnalysisResultData)

    fun sendInProgressStatus(pullRequest: PullRequest)
}