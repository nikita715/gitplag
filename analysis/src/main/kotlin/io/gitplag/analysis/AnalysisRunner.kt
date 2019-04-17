package io.gitplag.analysis

import io.gitplag.analysis.analyzer.Analyzer
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.entity.Analysis
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.manager.AnalysisResultDataManager
import mu.KotlinLogging

/**
 * Main analysis class
 */
class AnalysisRunner(
    private val analyzers: Map<AnalyzerProperty, Analyzer>,
    private val analysisResultDataManager: AnalysisResultDataManager
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Run analysis with [settings]
     */
    fun run(settings: AnalysisSettings): Analysis {
        val analysisService = analyzers.getValue(settings.analyzer)
        return logger.loggedAnalysis(settings) { analysisService.analyze(settings) }
            .let { analysisResultDataManager.saveAnalysis(settings, it) }
    }

    /**
     * Run all analyzes with [settingsList]
     */
    fun run(settingsList: List<AnalysisSettings>): List<Analysis> =
        settingsList.map { logger.loggedAnalysis(it) { run(it) } }

}