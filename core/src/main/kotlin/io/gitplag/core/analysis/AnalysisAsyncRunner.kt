package io.gitplag.core.analysis

import com.fasterxml.jackson.databind.ObjectMapper
import io.gitplag.analysis.AnalysisRunner
import io.gitplag.core.websocket.NotificationService
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.dto.AnalysisResultDto
import io.gitplag.util.sendAnalysisResult
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

/**
 * The class that asynchronously calls the [AnalysisRunner]
 */
@Component
class AnalysisAsyncRunner(
    private val analysisRunner: AnalysisRunner,
    private val notificationService: NotificationService
) {

    private val logger = KotlinLogging.logger {}
    private val objectMapper = ObjectMapper()

    /**
     * Initiate analysis of the [repository] async
     */
    @Async("analysisTaskExecutor")
    fun run(settings: AnalysisSettings) {
        notificationService.notify("Started analysis of repo ${settings.repository.name}")
        val analysis = analysisRunner.run(settings)
        notificationService.notify("Ended analysis #${analysis.id} of repo ${analysis.repository.name}")
    }

    /**
     * Async runner of an analysis
     */
    @Async("analysisTaskExecutor")
    fun runAndRespond(analysisSettings: AnalysisSettings, responseUrl: String?) {
        notificationService.notify("Started analysis of repo ${analysisSettings.repository.name}")
        val result = AnalysisResultDto(analysisRunner.run(analysisSettings))
        if (responseUrl != null) {
            val body = objectMapper.writeValueAsString(result)
            sendAnalysisResult(url = responseUrl, body = body)
        }
        notificationService.notify("Ended analysis #${result.id} of repo ${result.repoName}")
    }
}