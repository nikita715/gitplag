package ru.nikstep.redink.analysis

import mu.KotlinLogging
import ru.nikstep.redink.analysis.analyser.Analyser
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.entity.Analysis
import ru.nikstep.redink.model.enums.AnalyserProperty
import ru.nikstep.redink.model.manager.AnalysisResultDataManager

/**
 * Main analysis class
 */
class AnalysisRunner(
    private val analysers: Map<AnalyserProperty, Analyser>,
    private val analysisResultDataManager: AnalysisResultDataManager
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Run analysis with [analysisSettings]
     */
    fun run(settings: AnalysisSettings): Analysis {
        val analysisService = analysers.getValue(settings.analyser)
        return logger.loggedAnalysis(settings) { analysisService.analyse(settings) }
            .let { analysisResultDataManager.saveAnalysis(settings, it) }
    }

    /**
     * Run analysis with [analysisSettings]
     */
    fun run(analysisSettings: List<AnalysisSettings>): List<Analysis> =
        analysisSettings.map { logger.loggedAnalysis(it) { run(it) } }

}