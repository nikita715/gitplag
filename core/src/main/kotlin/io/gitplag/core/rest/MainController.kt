package io.gitplag.core.rest

import io.gitplag.analysis.AnalysisRunner
import io.gitplag.core.analysis.AnalysisAsyncRunner
import io.gitplag.git.payload.PayloadProcessor
import io.gitplag.git.rest.GitRestManager
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.dto.AnalysisPairDto
import io.gitplag.model.dto.FileDto
import io.gitplag.model.dto.RepositoryDto
import io.gitplag.model.entity.Analysis
import io.gitplag.model.entity.BaseFileRecord
import io.gitplag.model.entity.Repository
import io.gitplag.model.entity.SolutionFileRecord
import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.AnalysisPairRepository
import io.gitplag.model.repo.AnalysisRepository
import io.gitplag.model.repo.BaseFileRecordRepository
import io.gitplag.model.repo.SolutionFileRecordRepository
import io.gitplag.util.RandomGenerator
import io.gitplag.util.innerRegularFiles
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import java.io.File

/**
 * Controller for the frontend
 */
@RestController
@RequestMapping("/api")
class MainController(
    private val analysisRepository: AnalysisRepository,
    private val repositoryDataManager: RepositoryDataManager,
    private val analysisPairRepository: AnalysisPairRepository,
    private val randomGenerator: RandomGenerator,
    private val analysisRunner: AnalysisRunner,
    private val analysisAsyncRunner: AnalysisAsyncRunner,
    @Qualifier("gitRestManagers") private val restManagers: Map<GitProperty, GitRestManager>,
    @Qualifier("payloadProcessors") private val payloadProcessors: Map<GitProperty, PayloadProcessor>,
    private val solutionFileRecordRepository: SolutionFileRecordRepository,
    private val baseFileRecordRepository: BaseFileRecordRepository,
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

    class AnalysisDto(
        val id: Long,
        val branch: String,
        val analyzer: AnalyzerProperty?,
        val language: Language?,
        val mode: AnalysisMode?,
        val responseUrl: String?
    )

    /**
     * Initiate the analysis
     */
    @PostMapping("/repositories/{id}/analyze")
    fun analyze(@RequestBody dto: AnalysisDto): Analysis? {
        val repoValue = repositoryDataManager.findById(dto.id) ?: return null
        return analysisRunner.run(
            AnalysisSettings(
                repoValue,
                dto.branch,
                language = dto.language,
                analyzer = dto.analyzer,
                mode = dto.mode
            )
        )
    }

    /**
     * Initiate the analysis async
     */
    @PostMapping("/repositories/{id}/analyzeWithNoResponse")
    fun analyzeDetached(@RequestBody dto: AnalysisDto): Boolean {
        val repoValue = repositoryDataManager.findById(dto.id) ?: return false
        analysisAsyncRunner.runAndRespond(
            AnalysisSettings(
                repoValue,
                dto.branch,
                language = dto.language,
                analyzer = dto.analyzer,
                mode = dto.mode
            ), dto.responseUrl
        )
        return true
    }

    @PutMapping("/repositories/{id}")
    fun manageRepo(@PathVariable id: Long, @RequestBody dto: RepositoryDto): Repository {
        val storedRepo = repositoryDataManager.findById(id)
        return if (storedRepo == null) {
            repositoryDataManager.create(dto)
        } else {
            repositoryDataManager.update(storedRepo, dto)
        }
    }

    @PostMapping("/repositories/{id}/updateFiles")
    fun updateFilesOfRepo(@PathVariable id: Long): ComposedFiles? {
        val repository = repositoryDataManager.findById(id)

        return if (repository != null) {
            val gitRestManager = restManagers.getValue(repository.gitService)
            val payloadProcessor = payloadProcessors.getValue(repository.gitService)
            gitRestManager.cloneRepository(repository)
            payloadProcessor.downloadAllPullRequestsOfRepository(repository)
            ComposedFiles(
                bases = baseFileRecordRepository.findAllByRepo(repository),
                solutions = solutionFileRecordRepository.findAllByRepo(repository)
            )
        } else null
    }

    data class ComposedFiles(val bases: List<BaseFileRecord>, val solutions: List<SolutionFileRecord>)

    @PostMapping("/repositories/{id}/baseFiles")
    fun getLocalBases(@RequestBody dto: LocalFileDto): List<BaseFileRecord>? {
        val repo = repositoryDataManager.findById(dto.id)
        return if (repo != null) baseFileRecordRepository.findAllByRepo(repo)
            .filter {
                ((dto.branch == null) || (it.branch == dto.branch)) && ((dto.fileName == null) || (it.fileName == dto.fileName))
            }
        else null
    }

    @PostMapping("/repositories/{id}/solutionFiles")
    fun getLocalSolutions(@RequestBody dto: LocalFileDto): List<SolutionFileRecord>? {
        val repo = repositoryDataManager.findById(dto.id)
        return if (repo != null) solutionFileRecordRepository.findAllByRepo(repo)
            .filter {
                ((dto.branch == null) || (it.pullRequest.sourceBranchName == dto.branch))
                        && ((dto.fileName == null) || (it.fileName == dto.fileName))
                        && ((dto.student == null) || (it.pullRequest.creatorName == dto.student))
            }
        else null
    }

    class LocalFileDto(
        val id: Long,
        val branch: String?,
        val fileName: String?,
        val student: String?
    )

}