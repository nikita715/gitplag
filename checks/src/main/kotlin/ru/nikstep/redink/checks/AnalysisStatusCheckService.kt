package ru.nikstep.redink.checks

import ru.nikstep.redink.model.entity.PullRequest

interface AnalysisStatusCheckService {

    fun send(pullRequest: PullRequest, analysisData: AnalysisResultData)

    fun sendInProgressStatus(pullRequest: PullRequest)
}