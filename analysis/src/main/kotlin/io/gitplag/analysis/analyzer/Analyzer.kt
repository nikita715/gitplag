package io.gitplag.analysis.analyzer

import io.gitplag.model.data.AnalysisResult
import io.gitplag.model.data.AnalysisSettings

/**
 * Analyzer for plagiarism in pull requests
 */
interface Analyzer {

    /**
     * Analyze the branch for plagiarism
     *
     * @return student-to-student matches for each file
     */
    fun analyze(settings: AnalysisSettings): AnalysisResult
}