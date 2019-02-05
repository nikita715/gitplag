package ru.nikstep.redink.analysis

import ru.nikstep.redink.data.AnalysisResult
import ru.nikstep.redink.model.entity.PullRequest

interface AnalysisService {
    fun analyse(prData: PullRequest): Set<AnalysisResult>
}