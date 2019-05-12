package io.gitplag.core.rest

import io.gitplag.analysis.AnalysisRunner
import io.gitplag.core.analysis.AnalysisAsyncRunner
import io.gitplag.core.websocket.NotificationService
import io.gitplag.git.payload.PayloadProcessor
import io.gitplag.git.rest.GitRestManager
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.dto.*
import io.gitplag.model.entity.BaseFileRecord
import io.gitplag.model.entity.Repository
import io.gitplag.model.entity.SolutionFileRecord
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.BaseFileRecordRepository
import io.gitplag.model.repo.BranchRepository
import io.gitplag.model.repo.PullRequestRepository
import io.gitplag.model.repo.SolutionFileRecordRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

/**
 * Controller for repositories
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
    @Qualifier("payloadProcessors") private val payloadProcessors: Map<GitProperty, PayloadProcessor>,
    private val branchRepository: BranchRepository,
    private val notificationService: NotificationService
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
    fun analyze(@PathVariable id: Long, @RequestBody dto: AnalysisDto): AnalysisResultDto? {
        try {
            val repoValue = repositoryDataManager.findById(dto.repoId)
            if (repoValue == null) {
                notificationService.notify("Repository with id = $id is not found")
                return null
            }
            notificationService.notify("Started analysis of repo ${repoValue.name}")
            val resultDto = AnalysisResultDto(
                analysisRunner.run(
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
            )
            notificationService.notify("Ended analysis #${resultDto.id} of repo ${repoValue.name}")
            return resultDto
        } catch (e: Exception) {
            notificationService.notify(e.message)
            throw ApiException(e)
        }
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
        val updatedRepo = if (storedRepo != null) {
            repositoryDataManager.update(storedRepo, dto)
        } else null
        if (updatedRepo == null) {
            notificationService.notify("Repo with id $id not found")
            return null
        } else {
            notificationService.notify("Updated repo with id $id")
            return updatedRepo
        }
    }

    /**
     * Create a repo
     */
    @PostMapping("/repositories")
    fun createRepo(@RequestBody dto: RepositoryDto): Repository {
        val repository = repositoryDataManager.create(dto)
        notificationService.notify("Created repo ${repository.name} with id ${repository.id}")
        return repository
    }

    /**
     * Trigger download of files of the repo
     */
    @PostMapping("/repositories/{id}/updateFiles")
    fun updateFilesOfRepo(@PathVariable id: Long): RepositoryFilesInfoDto? {
        val repository = repositoryDataManager.findById(id)

        return if (repository != null) {
            val gitRestManager = restManagers.getValue(repository.gitService)
            val payloadProcessor = payloadProcessors.getValue(repository.gitService)
            gitRestManager.cloneRepository(repository)
            payloadProcessor.downloadAllPullRequestsOfRepository(repository)
            RepositoryFilesInfoDto(
                bases = basesToDto(baseFileRecordRepository.findAllByRepo(repository)),
                solutions = solutionsToDto(solutionFileRecordRepository.findAllByRepo(repository))
            )
        } else null
    }

    /**
     * Get downloaded base and solution files of the repo
     */
    @GetMapping("/repositories/{id}/files")
    fun getFilesOfRepo(@PathVariable id: Long): RepositoryFilesInfoDto? {
        val repository = repositoryDataManager.findById(id)
        return if (repository != null) {
            RepositoryFilesInfoDto(
                bases = basesToDto(baseFileRecordRepository.findAllByRepo(repository)),
                solutions = solutionsToDto(solutionFileRecordRepository.findAllByRepo(repository))
            )
        } else null
    }

    /**
     * Get downloaded base files of the repo
     */
    @GetMapping("/repositories/{id}/baseFiles")
    fun getLocalBases(@PathVariable id: Long): List<BaseBranchInfoDto> {
        val repo = repositoryDataManager.findById(id)
        return if (repo != null) basesToDto(baseFileRecordRepository.findAllByRepo(repo)) else emptyList()
    }

    /**
     * Get downloaded base files of the repo
     */
    @PostMapping("/repositories/{id}/baseFiles")
    fun getLocalBases(@PathVariable id: Long, @RequestBody dto: LocalFileDto): List<BaseBranchInfoDto> {
        val repo = repositoryDataManager.findById(id)
        return if (repo != null) basesToDto(baseFileRecordRepository.findAllByRepo(repo)
            .filter {
                ((dto.branch == null) || (it.branch == dto.branch)) && ((dto.fileName == null) || (it.fileName == dto.fileName))
            })
        else emptyList()
    }

    /**
     * Get downloaded solution files of the repo
     */
    @GetMapping("/repositories/{id}/solutionFiles")
    fun getLocalSolutions(@PathVariable id: Long): List<SolutionBranchInfoDto> {
        val repo = repositoryDataManager.findById(id)
        return if (repo != null) solutionsToDto(solutionFileRecordRepository.findAllByRepo(repo)) else emptyList()
    }

    /**
     * Get downloaded solution files of the repo
     */
    @PostMapping("/repositories/{id}/solutionFiles")
    fun getLocalSolutions(@PathVariable id: Long, @RequestBody dto: LocalFileDto): List<SolutionBranchInfoDto> {
        val repo = repositoryDataManager.findById(id)
        return if (repo != null) solutionsToDto(solutionFileRecordRepository.findAllByRepo(repo)
            .filter {
                ((dto.branch == null) || (it.pullRequest.sourceBranchName == dto.branch))
                        && ((dto.fileName == null) || (it.fileName == dto.fileName))
                        && ((dto.student == null) || (it.pullRequest.creatorName == dto.student))
            })
        else emptyList()
    }

    fun solutionsToDto(solutionRecords: Collection<SolutionFileRecord>) =
        solutionRecords.groupBy { it.pullRequest.sourceBranchName }.map { branch ->
            SolutionBranchInfoDto(
                sourceBranch = branch.key,
                students = branch.value.groupBy { it.pullRequest }.map { pullRequest ->
                    StudentFilesDto(
                        student = pullRequest.key.creatorName,
                        updated = pullRequest.key.updatedAt,
                        files = pullRequest.value.map { FileInfoDto(it) }
                    )
                }
            )
        }

    fun basesToDto(solutionRecords: Collection<BaseFileRecord>) =
        solutionRecords.groupBy { it.branch }.map { entry ->
            BaseBranchInfoDto(
                branch = entry.key,
                files = entry.value.map { FileInfoDto(it) },
                updated = branchRepository.findByRepositoryAndName(entry.value.first().repo, entry.key)?.updatedAt
                    ?: LocalDateTime.MIN
            )
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