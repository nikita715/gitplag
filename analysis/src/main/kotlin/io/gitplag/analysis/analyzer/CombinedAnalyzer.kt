package io.gitplag.analysis.analyzer

import io.gitplag.analysis.mergeAnalysisResults
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.data.AnalysisResult
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.data.PreparedAnalysisData
import io.gitplag.model.enums.AnalyzerProperty
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Supplier

class CombinedAnalyzer(
    private val analyzers: Map<AnalyzerProperty, Analyzer>,
    sourceCodeStorage: SourceCodeStorage,
    analysisResultFilesDir: String
) : AbstractAnalyzer(sourceCodeStorage, analysisResultFilesDir) {
    private val executor: Executor = Executors.newCachedThreadPool()

    override fun analyze(settings: AnalysisSettings, analysisFiles: PreparedAnalysisData) =
        mergeAnalysisResults(analyzers.map { analyzer ->
            CompletableFuture.supplyAsync<AnalysisResult>(
                Supplier { (analyzer.value as AbstractAnalyzer).analyze(settings, analysisFiles) },
                executor
            )
        }.map { it.join() })

}