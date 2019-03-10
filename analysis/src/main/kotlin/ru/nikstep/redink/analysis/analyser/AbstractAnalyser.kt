package ru.nikstep.redink.analysis.analyser

import ru.nikstep.redink.analysis.PreparedAnalysisFiles
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.entity.Repository

/**
 * Common implementation of the [Analyser]
 */
abstract class AbstractAnalyser(private val solutionStorage: SolutionStorage) :
    Analyser {

    override fun analyse(repository: Repository): Collection<AnalysisResult> =
        solutionStorage.loadAllBasesAndSolutions(repository)
            .flatMap { analyseOneFile(repository, it) }

    abstract fun analyseOneFile(
        repository: Repository,
        analysisFiles: PreparedAnalysisFiles
    ): Iterable<AnalysisResult>

}