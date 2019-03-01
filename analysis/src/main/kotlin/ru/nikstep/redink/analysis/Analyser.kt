package ru.nikstep.redink.analysis

import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.entity.PullRequest

interface Analyser {
    fun analyse(pullRequest: PullRequest): Collection<AnalysisResult>
}