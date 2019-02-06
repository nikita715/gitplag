package ru.nikstep.redink.checks

import ru.nikstep.redink.model.entity.PullRequest

interface AnalysisStatusCheckService {

    fun send(prData: PullRequest, analysisData: AnalysisResultData)

}