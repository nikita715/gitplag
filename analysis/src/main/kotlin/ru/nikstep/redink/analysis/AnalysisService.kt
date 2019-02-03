package ru.nikstep.redink.analysis

import ru.nikstep.redink.data.PullRequestData

interface AnalysisService {
    fun analyse(prData: PullRequestData)
}