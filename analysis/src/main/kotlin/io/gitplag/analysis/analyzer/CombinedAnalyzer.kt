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
        val jplagAnalysis =
            CompletableFuture.supplyAsync<AnalysisResult>(Supplier { jPlagAnalyzer.analyze(settings) }, executor)
        return mergeAnalysisResults(mossAnalyzer.analyze(settings), jplagAnalysis.join())
    }
}