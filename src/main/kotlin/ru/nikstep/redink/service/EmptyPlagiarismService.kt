package ru.nikstep.redink.service

import ru.nikstep.redink.entity.PullRequest

class EmptyPlagiarismService : PlagiarismService {
    override fun analyze(pullRequest: PullRequest)
//            : AnalysisResult
    {
//        return AnalysisResult(URL(""), arrayListOf(), setOf())
    }
}