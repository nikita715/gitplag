package io.gitplag.analysis.analyzer

import io.gitplag.analysis.mergeAnalysisResults
import io.gitplag.model.data.AnalysisResult
import io.gitplag.model.data.AnalysisSettings
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Supplier

class CombinedAnalyzer(
    private val mossAnalyzer: MossAnalyzer,
    private val jPlagAnalyzer: JPlagAnalyzer
) : Analyzer {
    private val executor: Executor = Executors.newCachedThreadPool()

    override fun analyze(settings: AnalysisSettings): AnalysisResult {
        val mossAnalysis =
            CompletableFuture.supplyAsync<AnalysisResult>(Supplier { mossAnalyzer.analyze(settings) }, executor)
        return mergeAnalysisResults(jPlagAnalyzer.analyze(settings), mossAnalysis.join())
    }
}