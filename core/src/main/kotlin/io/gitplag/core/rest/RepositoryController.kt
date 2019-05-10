package io.gitplag.core.rest

import io.gitplag.analysis.AnalysisRunner
import io.gitplag.core.analysis.AnalysisAsyncRunner
import io.gitplag.git.payload.PayloadProcessor
import io.gitplag.git.rest.GitRestManager
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.dto.*
import io.gitplag.model.entity.Analysis
import io.gitplag.model.entity.BaseFileRecord
import io.gitplag.model.entity.Repository
import io.gitplag.model.entity.SolutionFileRecord
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.BaseFileRecordRepository
import io.gitplag.model.repo.PullRequestRepository
import io.gitplag.model.repo.SolutionFileRecordRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*

/**
 * Controller for the frontend
 */
@RestController
@RequestMapping("/api")
class RepositoryController(
    private val repositoryDataManager: RepositoryDataManager,
    private val pullRequestRepository: PullRequestRepository,
    private val analysisRunner: AnalysisRunner,
    private val analysisAsyncRunner: AnalysisAsyncRunner,
    private val solutionFileRecordRepository: SolutionFileRecordRepository,
    private val baseFileRecordRepository: BaseFileRecordRepository,
    @Qualifier("gitRestManagers") private val restManagers: Map<GitProperty, GitRestManager>,
    @Qualifier("payloadProcessors") private val payloadProcessors: Map<GitProperty, PayloadProcessor>
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Get all repositories
     */
    @GetMapping("/repositories")
    fun getAllRepositories() = repositoryDataManager.findAll().sortedBy { it.id }

    /**
     * Get the repo
     */
    @GetMapping("/repositories/{id}")
    fun getRepo(@PathVariable id: Long) = repositoryDataManager.findById(id)

    /**
     * Get analyzes of the repo
     */
    @GetMapping("/repositories/{id}/analyzes")
    fun getRepository(@PathVariable id: Long) =
        repositoryDataManager.findById(id)?.analyzes?.map { AnalysisResultDto(it) }?.sortedBy { it.id } ?: emptyList()


    /**
     * Initiate the analysis
     */
    @PostMapping("/repositories/{id}/analyze")
    fun analyze(@RequestBody dto: AnalysisDto): Analysis? {
        val repoValue = repositoryDataManager.findById(dto.repoId) ?: return null
        return analysisRunner.run(
            AnalysisSettings(
                repoValue,
                dto.branch,
                language = dto.language,
                analyzer = dto.analyzer,
                mode = dto.mode,
                parameters = dto.parameters,
                updateFiles = dto.updateFiles
            )
        )
    }

    /**
     * Initiate the analysis async
     */
    @PostMapping("/repositories/{id}/analyzeWithNoResponse")
    fun analyzeDetached(@RequestBody dto: AnalysisDto): Boolean {
        val repoValue = repositoryDataManager.findById(dto.repoId) ?: return false
        analysisAsyncRunner.runAndRespond(
            AnalysisSettings(
                repoValue,
                dto.branch,
                language = dto.language,
                analyzer = dto.analyzer,
                mode = dto.mode,
                parameters = dto.parameters,
                updateFiles = dto.updateFiles
            ), dto.responseUrl
        )
        return true
    }

    /**
     * Update the repo
     */
    @PutMapping("/repositories/{id}")
    fun editRepo(@PathVariable id: Long, @RequestBody dto: RepositoryDto): Repository? {
        val storedRepo = repositoryDataManager.findById(id)
        return if (storedRepo != null) {
            repositoryDataManager.update(storedRepo, dto)
        } else null
    }

    /**
     * Create a repo
     */
    @PostMapping("/repositories")
    fun createRepo(@RequestBody dto: RepositoryDto): Repository = repositoryDataManager.create(dto)

    /**
     * Trigger download of files of the repo
     */
    @PostMapping("/repositories/{id}/updateFiles")
    fun updateFilesOfRepo(@PathVariable id: Long): FileInfoDto? {
        val repository = repositoryDataManager.findById(id)

        return if (repository != null) {
            val gitRestManager = restManagers.getValue(repository.gitService)
            val payloadProcessor = payloadProcessors.getValue(repository.gitService)
            gitRestManager.cloneRepository(repository)
            payloadProcessor.downloadAllPullRequestsOfRepository(repository)
            FileInfoDto(
                bases = baseFileRecordRepository.findAllByRepo(repository).map { BaseFileInfoDto(it) },
                solutions = solutionFileRecordRepository.findAllByRepo(repository).map { SolutionFileInfoDto(it) }
            )
        } else null
    }

    /**
     * Get downloaded base and solution files of the repo
     */
    @PostMapping("/repositories/{id}/files")
    fun getFilesOfRepo(@PathVariable id: Long): FileInfoDto? {
        val repository = repositoryDataManager.findById(id)

        return if (repository != null) {
            FileInfoDto(
                bases = baseFileRecordRepository.findAllByRepo(repository).map { BaseFileInfoDto(it) },
                solutions = solutionFileRecordRepository.findAllByRepo(repository).map { SolutionFileInfoDto(it) }
            )
        } else null
    }

    /**
     * Get downloaded base files of the repo
     */
    @PostMapping("/repositories/{id}/baseFiles")
    fun getLocalBases(@RequestBody dto: LocalFileDto): List<BaseFileRecord>? {
        val repo = repositoryDataManager.findById(dto.id)
        return if (repo != null) baseFileRecordRepository.findAllByRepo(repo)
            .filter {
                ((dto.branch == null) || (it.branch == dto.branch)) && ((dto.fileName == null) || (it.fileName == dto.fileName))
            }
        else null
    }

    /**
     * Get downloaded solution files of the repo
     */
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

    /**
     * Get pull requests of the repo
     */
    @GetMapping("/repositories/{id}/pulls")
    fun getPullRequests(@PathVariable id: Long) =
        pullRequestRepository.findAllByRepoId(id).map {
            it.run { PullRequestDto(this.id, number, creatorName, sourceBranchName, mainBranchName) }
        }

}