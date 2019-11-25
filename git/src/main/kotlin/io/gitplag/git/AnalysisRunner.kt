package io.gitplag.git

import io.gitplag.analysis.AnalysisRunner
import io.gitplag.analysis.analyzer.Analyzer
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.git.payload.GitManager
import io.gitplag.model.data.AnalysisResult
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.entity.Analysis
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.AnalysisResultDataManager
import mu.KotlinLogging

/**
 * Main analysis class
 */
class GitAnalysisRunner(
    private val analyzers: Map<AnalyzerProperty, Analyzer>,
    private val payloadProcessors: Map<GitProperty, GitManager>,
    private val analysisResultDataManager: AnalysisResultDataManager,
    private val sourceCodeStorage: SourceCodeStorage
) : AnalysisRunner {
    private val logger = KotlinLogging.logger {}

    /**
     * Run analysis with [settings]
     */
    override fun run(settings: AnalysisSettings): Analysis {
        if (settings.updateFiles) {
            payloadProcessors.getValue(settings.repository.gitService)
                .downloadAllPullRequestsOfRepository(settings.repository)
        }
        val analysisService = analyzers.getValue(settings.analyzer)
        val analysisResult: AnalysisResult
        try {
            analysisResult = logger.loggedAnalysis(settings) { analysisService.analyze(settings) }
        } catch (e: Exception) {
            sourceCodeStorage.deleteAnalysisFiles(settings.repository.name, settings.executionDate, settings.analyzer)
            throw e
        }
        return analysisResult.let { analysisResultDataManager.saveAnalysis(settings, it) }
    }

    /**
     * Run all analyzes with [settingsList]
     */
    override fun run(settingsList: List<AnalysisSettings>): List<Analysis> =
        settingsList.map { logger.loggedAnalysis(it) { run(it) } }

}