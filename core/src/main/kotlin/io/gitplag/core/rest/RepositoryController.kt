package io.gitplag.core.rest

import io.gitplag.analysis.AnalysisRunner
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.core.async.AnalysisAsyncRunner
import io.gitplag.core.async.AsyncFileUploader
import io.gitplag.core.websocket.NotificationService
import io.gitplag.git.payload.PayloadProcessor
import io.gitplag.git.rest.GitRestManager
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.dto.*
import io.gitplag.model.entity.BaseFileRecord
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
    private val notificationService: NotificationService,
    private val asyncFileUploader: AsyncFileUploader,
    private val sourceCodeStorage: SourceCodeStorage
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Get all repositories
     */
    @GetMapping("/repositories")
    fun getAllRepositories() = repositoryDataManager.findAll().map(::OutputRepositoryDto).sortedBy { it.id }

    /**
     * Get the repo
     */
    @GetMapping("/repositories/{id}")
    fun getRepo(@PathVariable id: Long) = repositoryDataManager.findById(id)?.let(::OutputRepositoryDto)

    /**
     * Get analyzes of the repo
     */
    @GetMapping("/repositories/{id}/analyzes")
    fun getRepositoryAnalyzes(@PathVariable id: Long) =
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
    @PostMapping("/repositories/{id}/analyze/detached")
    fun analyzeDetached(@PathVariable id: Long, @RequestBody dto: AnalysisDto): Boolean {
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
    fun editRepo(@PathVariable id: Long, @RequestBody dto: InputRepositoryDto): OutputRepositoryDto? {
        val storedRepo = repositoryDataManager.findById(id)
        val updatedRepo = if (storedRepo != null) {
            repositoryDataManager.update(storedRepo, dto)
        } else null
        if (updatedRepo == null) {
            notificationService.notify("Repo with id $id not found")
            return null
        } else {
            notificationService.notify("Updated repo with id $id")
            return OutputRepositoryDto(updatedRepo)
        }
    }

    /**
     * Create a repo
     */
    @PostMapping("/repositories")
    fun createRepo(@RequestBody dto: InputRepositoryDto): OutputRepositoryDto? {
        val repository = payloadProcessors.getValue(dto.git).createRepo(dto)
        return if (repository != null) {
            notificationService.notify("Created repo ${repository.name} with id ${repository.id}")
            OutputRepositoryDto(repository)
        } else {
            notificationService.notify("Repository with name ${dto.name} is not found in ${dto.git.name.toLowerCase()}")
            null
        }
    }

    /**
     * Create a repo
     */
    @DeleteMapping("/repositories/{id}")
    fun deleteRepo(@PathVariable id: Long) {
        val repository = repositoryDataManager.findById(id)
        if (repository != null) {
            repository.analyzes.forEach { analysis ->
                sourceCodeStorage.deleteAnalysisFiles(analysis)
            }
            repositoryDataManager.delete(repository)
            notificationService.notify("Deleted repo ${repository.name} with id ${repository.id}")
        }
    }

    /**
     * Trigger download of files of the repo
     */
    @GetMapping("/repositories/{id}/files/update")
    fun updateFilesOfRepo(@PathVariable id: Long): RepositoryFilesInfoDto? {
        val repository = repositoryDataManager.findById(id)
        return if (repository != null) {
            notificationService.notify("Started upload of files from repo ${repository.name}")
            val gitRestManager = restManagers.getValue(repository.gitService)
            val payloadProcessor = payloadProcessors.getValue(repository.gitService)
            gitRestManager.cloneRepository(repository)
            payloadProcessor.downloadAllPullRequestsOfRepository(repository)
            notificationService.notify("Ended upload of files from repo ${repository.name}")
            RepositoryFilesInfoDto(
                bases = basesToDto(baseFileRecordRepository.findAllByRepo(repository)),
                solutions = solutionsToDto(solutionFileRecordRepository.findAllByRepo(repository))
            )
        } else null
    }

    /**
     * Trigger download of files of the repo
     */
    @GetMapping("/repositories/{id}/files/update/detached")
    fun updateFilesOfRepoAsync(@PathVariable id: Long): Boolean {
        val repository = repositoryDataManager.findById(id)

        if (repository != null) {
            asyncFileUploader.uploadFiles(repository)
            return true
        }

        return false
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
    @GetMapping("/repositories/{id}/bases")
    fun getLocalBases(@PathVariable id: Long): List<BaseBranchInfoDto> {
        val repo = repositoryDataManager.findById(id)
        return if (repo != null) basesToDto(baseFileRecordRepository.findAllByRepo(repo)) else emptyList()
    }

    /**
     * Get downloaded base files of the repo
     */
    @PostMapping("/repositories/{id}/bases")
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
    @GetMapping("/repositories/{id}/solutions")
    fun getLocalSolutions(@PathVariable id: Long): List<SolutionBranchInfoDto> {
        val repo = repositoryDataManager.findById(id)
        return if (repo != null) solutionsToDto(solutionFileRecordRepository.findAllByRepo(repo)) else emptyList()
    }

    /**
     * Get downloaded solution files of the repo
     */
    @PostMapping("/repositories/{id}/solutions")
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

    private fun solutionsToDto(solutionRecords: Collection<SolutionFileRecord>) =
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

    private fun basesToDto(solutionRecords: Collection<BaseFileRecord>) =
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
        pullRequestRepository.findAllByRepoId(id).map { PullRequestDto(it) }

    //    @PostMapping("/repositories/{id}/bases/delete")
    fun deleteBaseFiles(@PathVariable id: Long, @RequestBody ids: List<Long>) {
        val repo = repositoryDataManager.findById(id) ?: return
        val bases = baseFileRecordRepository.findAllById(ids)
        bases.map { it.branch }.toSet().forEach { branchName ->
            val branch = branchRepository.findByRepositoryAndName(repo, branchName)
            if (branch != null) branchRepository.delete(branch)
        }
        bases.forEach {
            sourceCodeStorage.deleteBaseFile(repo, it.branch, it.fileName)
        }
        baseFileRecordRepository.deleteAll(bases)
    }

    //    @PostMapping("/repositories/{id}/solutions/delete")
    fun deleteSolutionFiles(@PathVariable id: Long, @RequestBody ids: List<Long>) {
        val repo = repositoryDataManager.findById(id) ?: return
        val solutions = solutionFileRecordRepository.findAllById(ids)
        solutions.forEach {
            sourceCodeStorage.deleteSolutionFile(
                repo,
                it.pullRequest.sourceBranchName,
                it.pullRequest.creatorName,
                it.fileName
            )
        }
        solutionFileRecordRepository.deleteAll(solutions)
    }

    @DeleteMapping("/repositories/{id}/bases/delete")
    fun deleteAllBaseFiles(@PathVariable id: Long) {
        val repo = repositoryDataManager.findById(id) ?: return
        repo.branches.forEach { branch ->
            sourceCodeStorage.deleteAllBaseFiles(repo, branch.name)
        }
        branchRepository.deleteAll(repo.branches)
        baseFileRecordRepository.deleteAllByRepo(repo)
    }

    @DeleteMapping("/repositories/{id}/solutions/delete")
    fun deleteAllSolutionFiles(@PathVariable id: Long) {
        val repo = repositoryDataManager.findById(id) ?: return
        repo.pullRequests.forEach { pullRequest ->
            solutionFileRecordRepository.deleteAllByPullRequest(pullRequest)
            sourceCodeStorage.deleteAllSolutionFiles(repo, pullRequest.sourceBranchName, pullRequest.creatorName)
        }
        pullRequestRepository.deleteAll(repo.pullRequests)
    }
}
