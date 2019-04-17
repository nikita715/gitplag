package io.gitplag.analysis.analyzer

import io.gitplag.model.data.AnalysisResult
import io.gitplag.model.data.AnalysisSettings

/**
 * Analyzer for plagiarism in pull requests
 */
interface Analyzer {

    /**
     * Analyze the pull request for plagiarism
     *
     * @return student-to-student matches for each file
     */
    fun analyze(settings: AnalysisSettings): AnalysisResult

    companion object {
        fun repoInfo(analysisSettings: AnalysisSettings): String =
            analysisSettings.run { "Repo ${repository.name}, Branch $branch." }
    }
}