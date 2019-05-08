package io.gitplag.core.analysis

import com.fasterxml.jackson.databind.ObjectMapper
import io.gitplag.analysis.AnalysisRunner
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.util.sendAnalysisResult
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

/**
 * The class that asynchronously calls the [AnalysisRunner]
 */
@Component
class AnalysisAsyncRunner(private val analysisRunner: AnalysisRunner) {

    private val logger = KotlinLogging.logger {}
    private val objectMapper = ObjectMapper()

    /**
     * Initiate analysis of the [repository] async
     */
    @Async("analysisTaskExecutor")
    fun run(settings: AnalysisSettings) {
        analysisRunner.run(settings)
    }

    /**
     * Async runner of an analysis
     */
    @Async("analysisTaskExecutor")
    fun runAndRespond(analysisSettings: AnalysisSettings, responseUrl: String?) {
        val result = analysisRunner.run(analysisSettings)
        if (responseUrl != null) {
            val body = objectMapper.writeValueAsString(result)
            sendAnalysisResult(url = responseUrl, body = body)
        }
    }
}