package io.gitplag.core.rest

import io.gitplag.model.dto.AnalysisPairDto
import io.gitplag.model.dto.FileDto
import io.gitplag.model.entity.Analysis
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.AnalysisPairRepository
import io.gitplag.model.repo.AnalysisRepository
import io.gitplag.util.RandomGenerator
import io.gitplag.util.innerRegularFiles
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File

/**
 * Controller for the frontend
 */
@RestController
@RequestMapping("/api")
class AnalysisResultController(
    private val analysisRepository: AnalysisRepository,
    private val repositoryDataManager: RepositoryDataManager,
    private val analysisPairRepository: AnalysisPairRepository,
    private val randomGenerator: RandomGenerator,
    @Value("\${gitplag.analysisFilesDir}") private val analysisFilesDir: String,
    @Value("\${gitplag.graphUrl}") private val graphUrl: String,
    @Value("\${gitplag.serverUrl}") private val serverUrl: String
) {

    @GetMapping("/repositories")
    fun getAllRepositories(): MutableList<Repository>? = repositoryDataManager.findAll()

    @GetMapping("/repositories/{id}")
    fun getRepository(@PathVariable id: Long): List<Analysis>? = repositoryDataManager.findById(id)?.analyzes

    @GetMapping("/analyzes/{id}")
    fun getAnalysis(@PathVariable id: Long): Analysis? = analysisRepository.findById(id).orElse(null)

    @GetMapping("/analyzes/{analysisId}/pairs/{analysisPairId}")
    fun getAnalysisPair(@PathVariable analysisId: Long, @PathVariable analysisPairId: Long): AnalysisPairDto? {
        val analysis = analysisRepository.findById(analysisId).orElse(null)
        val analysisPair = analysisPairRepository.findById(analysisPairId).orElse(null)
        if (analysis == null || analysisPair == null) return null
        return AnalysisPairDto(
            findAnalysisFiles(analysis, analysisPair.student1),
            findAnalysisFiles(analysis, analysisPair.student2),
            analysisPair
        )
    }


    private fun findAnalysisFiles(analysis: Analysis, user: String): List<FileDto> =
        when (analysis.analyzer) {
            AnalyzerProperty.MOSS -> listOf(File("$analysisFilesDir/${analysis.hash}/$user").listFiles()[0])
            AnalyzerProperty.JPLAG -> File("$analysisFilesDir/${analysis.hash}/$user").innerRegularFiles()
        }.sortedBy { it.name }.map { FileDto(user, it.readText()) }
}