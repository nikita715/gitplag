package ru.nikstep.redink.analysis

import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.entity.PullRequest

abstract class AbstractAnalyser(private val solutionStorage: SolutionStorage, private val solutionsPath: String) :
    Analyser {

    override fun analyse(pullRequest: PullRequest): Collection<AnalysisResult> =
        solutionStorage.loadAllBasesAndSolutions(pullRequest)
            .flatMap { it.processFiles(pullRequest) }

    abstract fun PreparedAnalysisFiles.processFiles(pullRequest: PullRequest): Iterable<AnalysisResult>

}