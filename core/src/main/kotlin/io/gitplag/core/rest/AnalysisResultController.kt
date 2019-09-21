package io.gitplag.core.rest

import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.core.websocket.NotificationService
import io.gitplag.model.dto.AnalysisFilePairDto
import io.gitplag.model.dto.AnalysisPairDto
import io.gitplag.model.dto.AnalysisResultDto
import io.gitplag.model.dto.FileDto
import io.gitplag.model.entity.Analysis
import io.gitplag.model.repo.AnalysisPairRepository
import io.gitplag.model.repo.AnalysisRepository
import mu.KotlinLogging
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for analyzes
 */
@RestController
@RequestMapping("/api/analyzes")
class AnalysisResultController(
    private val analysisRepository: AnalysisRepository,
    private val analysisPairRepository: AnalysisPairRepository,
    private val sourceCodeStorage: SourceCodeStorage,
    private val notificationService: NotificationService
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Get the analysis result
     */
    @GetMapping("/{id}")
    fun getAnalysis(@PathVariable id: Long): AnalysisResultDto? =
        analysisRepository.findById(id).orElse(null)?.let { AnalysisResultDto(it) }

    /**
     * Get the analysis result
     */
    @DeleteMapping("/{id}")
    fun deleteAnalysis(@PathVariable id: Long) {
        val analysis: Analysis? = analysisRepository.findById(id).orElse(null)
        if (analysis != null) {
            analysisRepository.delete(analysis)
            sourceCodeStorage.deleteAnalysisFiles(analysis)
            logger.info { "Analysis #${analysis.id} has been deleted" }
            notificationService.notify("Deleted analysis #${analysis.id}")
        } else {
            logger.info { "Attempt to delete analysis with id #$id, not found" }
        }
    }

    /**
     * Get two files of the analysis result pair and matching lines
     */
    @GetMapping("/{analysisId}/pairs/{analysisPairId}")
    fun getAnalysisPair(@PathVariable analysisId: Long, @PathVariable analysisPairId: Long): AnalysisFilePairDto? {
        val analysis = analysisRepository.findById(analysisId).orElse(null)
        val analysisPair = analysisPairRepository.findById(analysisPairId)

        if (analysis == null || !analysisPair.isPresent) return null
        val pair = AnalysisPairDto(analysisPair.get())
        return AnalysisFilePairDto(
            getAnalysisFiles(analysis, analysisPair.get().student1),
            getAnalysisFiles(analysis, analysisPair.get().student2),
            pair
        )
    }

    private fun getAnalysisFiles(analysis: Analysis, user: String): List<FileDto> =
        sourceCodeStorage.getAnalysisFiles(analysis, user).map { FileDto(it.name, it.readLines()) }

}