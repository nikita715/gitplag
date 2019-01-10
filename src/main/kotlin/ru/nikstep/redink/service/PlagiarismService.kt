package ru.nikstep.redink.service

import ru.nikstep.redink.entity.PullRequest

interface PlagiarismService {
    fun analyze(pullRequest: PullRequest)
//            : AnalysisResult
}
