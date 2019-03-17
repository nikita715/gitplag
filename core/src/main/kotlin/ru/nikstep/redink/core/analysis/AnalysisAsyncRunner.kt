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
    fun run(settings: AnalysisSettings) {
        analysisRunner.run(settings)
    }

    /**
     * Async runner of analyzes
     */
    @Async("analysisTaskExecutor")
    fun runAndRespond(analysisSettings: List<AnalysisSettings>, responseUrl: String?) {
        val results = analysisRunner.run(analysisSettings)
        if (responseUrl != null) {
            val body = if (results.size == 1)
                objectMapper.writeValueAsString(results[0])
            else
                objectMapper.writeValueAsString(results)
            sendAnalysisResult(url = responseUrl, body = body)
        }
    }
}