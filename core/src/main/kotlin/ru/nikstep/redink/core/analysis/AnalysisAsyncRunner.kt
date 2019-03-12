package ru.nikstep.redink.core.analysis

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.util.sendAnalysisResult

@Component
class AnalysisAsyncRunner(private val analysisRunner: AnalysisRunner) {

    private val logger = KotlinLogging.logger {}
    private val objectMapper = ObjectMapper()

    /**
     * Initiate analysis of the [repository] async
     */
    @Async("analysisTaskExecutor")
    fun initiateAsync(settings: AnalysisSettings) {
        try {
            logger.loggedAnalysis(settings) {
                analysisRunner.run(settings)
            }
        } catch (e: Exception) {
            logger.exceptionAtAnalysisOf(e, settings)
        }
    }

    /**
     * Async runner of analyzes
     */
    @Async("analysisTaskExecutor")
    fun run(analysisSettings: AnalysisSettings, responseUrl: String?) {
        logger.loggedAnalysis(analysisSettings) {
            val analysis = analysisRunner.run(analysisSettings)
            if (responseUrl != null) sendAnalysisResult(
                url = responseUrl,
                body = objectMapper.writeValueAsString(analysis)
            )
        }
    }
}