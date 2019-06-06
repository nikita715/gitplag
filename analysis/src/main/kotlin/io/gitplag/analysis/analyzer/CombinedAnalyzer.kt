package io.gitplag.analysis.analyzer

import io.gitplag.analysis.analysisFilesDirectoryName
import io.gitplag.analysis.mergeAnalysisResults
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.data.AnalysisResult
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.data.PreparedAnalysisData
import io.gitplag.util.generateDir
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Supplier

class CombinedAnalyzer(
    private val mossAnalyzer: MossAnalyzer,
    private val jPlagAnalyzer: JPlagAnalyzer,
    private val sourceCodeStorage: SourceCodeStorage,
    private val analysisResultFilesDir: String
) : Analyzer {
    private val executor: Executor = Executors.newCachedThreadPool()

    override fun analyze(settings: AnalysisSettings, analysisFiles: PreparedAnalysisData): AnalysisResult {
        val jplagAnalysis =
            CompletableFuture.supplyAsync<AnalysisResult>(
                Supplier { jPlagAnalyzer.analyze(settings, analysisFiles) },
                executor
            )
        return mergeAnalysisResults(mossAnalyzer.analyze(settings, analysisFiles), jplagAnalysis.join())
    }

    override fun analyze(settings: AnalysisSettings): AnalysisResult {
        val directoryName = analysisFilesDirectoryName(settings)
        val fileDir = generateDir(analysisResultFilesDir, directoryName)
        val analysisFiles = sourceCodeStorage.loadBasesAndComposedSolutions(settings, fileDir)
        return analyze(settings, analysisFiles)
    }
}