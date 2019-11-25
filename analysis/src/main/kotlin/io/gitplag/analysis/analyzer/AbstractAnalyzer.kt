package io.gitplag.analysis.analyzer

import io.gitplag.analysis.analysisFilesDirectoryName
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.data.AnalysisResult
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.data.PreparedAnalysisData
import io.gitplag.util.generateDir

/**
 * Common analyzer class
 */
abstract class AbstractAnalyzer(
    private val sourceCodeStorage: SourceCodeStorage,
    private val analysisResultFilesDir: String
) : Analyzer {

    override fun analyze(settings: AnalysisSettings): AnalysisResult {
        val directoryName = analysisFilesDirectoryName(settings)
        val fileDir = generateDir(analysisResultFilesDir, directoryName)
        val analysisFiles = sourceCodeStorage.loadBasesAndSolutions(settings, fileDir)
        return analyze(settings, analysisFiles)
    }

    /**
     * Analyze the [analysisFiles] for plagiarism
     *
     * @return student-to-student matches for each file
     */
    abstract fun analyze(settings: AnalysisSettings, analysisFiles: PreparedAnalysisData): AnalysisResult
}