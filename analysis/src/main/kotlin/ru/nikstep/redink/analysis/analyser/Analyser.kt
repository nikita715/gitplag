package ru.nikstep.redink.analysis.analyser

import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.AnalysisSettings

/**
 * Analyzer for plagiarism in pull requests
 */
interface Analyser {

    /**
     * Analyse the pull request for plagiarism
     *
     * @return student-to-student matches for each file
     */
    fun analyse(analysisSettings: AnalysisSettings): AnalysisResult
}