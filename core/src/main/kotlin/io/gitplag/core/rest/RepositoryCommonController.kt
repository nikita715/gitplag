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
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Controller for repositories
 */
@Component
class RepositoryCommonController(
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
     * Get the repo
     */
    fun getRepo(repository: Repository?) = repository?.let(::OutputRepositoryDto)

    /**
     * Get analyzes of the repo
     */
    fun getRepositoryAnalyzes(repository: Repository?) =
        repository?.analyzes?.map { AnalysisResultDto(it) }?.sortedBy { it.id } ?: emptyList()

    /**
     * Initiate the analysis
     */
    fun analyze(repository: Repository?, dto: AnalysisDto): AnalysisResultDto? {
        try {
            if (repository == null) {
                notificationService.notify("Repository not found")
                return null
            }
            notificationService.notify("Started analysis of repo ${repository.name}")
            val resultDto = AnalysisResultDto(
                analysisRunner.run(
                    AnalysisSettings(
                        repository,
                        dto.branch,
                        analyzer = dto.analyzer,
                        language = dto.language,
                        analysisMode = dto.mode,
                        updateFiles = dto.updateFiles,
                        maxResultSize = dto.maxResultSize,
                        minResultPercentage = dto.minResultPercentage,
                        additionalRepositories = dto.additionalRepositories
                    )
                )
            )
            notificationService.notify("Ended analysis #${resultDto.id} of repo ${repository.name}")
            return resultDto
        } catch (e: Exception) {
            notificationService.notify(e.message)
            throw ApiException(e)
        }
    }

    /**
     * Initiate the analysis async
     */
    fun analyzeDetached(repository: Repository?, dto: AnalysisDto): Boolean {
        val repoValue = repository ?: return false
        analysisAsyncRunner.runAndRespond(
            AnalysisSettings(
                repoValue,
                dto.branch,
                analyzer = dto.analyzer,
                language = dto.language,
                analysisMode = dto.mode,
                updateFiles = dto.updateFiles,
                maxResultSize = dto.maxResultSize,
                minResultPercentage = dto.minResultPercentage,
                additionalRepositories = dto.additionalRepositories
            ), dto.responseUrl
        )
        return true
    }

    /**
     * Update the repo
     */
    fun editRepo(repository: Repository?, dto: InputRepositoryDto): OutputRepositoryDto? {
        val updatedRepo = if (repository != null) {
            repositoryDataManager.update(repository, dto)
        } else null
        return if (updatedRepo == null) {
            notificationService.notify("Repo not found")
            null
        } else {
            notificationService.notify("Updated repo with id ${updatedRepo.id}")
            OutputRepositoryDto(updatedRepo)
        }
    }

    /**
     * Create a repo
     */
    fun createRepo(dto: InputRepositoryDto): OutputRepositoryDto? {
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
    fun deleteRepo(repository: Repository?) {
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
    fun updateFilesOfRepo(repository: Repository?): RepositoryFilesInfoDto? {
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
    fun updateFilesOfRepoAsync(repository: Repository?): Boolean {
        if (repository != null) {
            asyncFileUploader.uploadFiles(repository)
            return true
        }
        return false
    }

    /**
     * Get downloaded base and solution files of the repo
     */
    fun getFilesOfRepo(repository: Repository?): RepositoryFilesInfoDto? {
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
    fun getLocalBases(repository: Repository?): List<BaseBranchInfoDto> {
        return if (repository != null) basesToDto(baseFileRecordRepository.findAllByRepo(repository)) else emptyList()
    }

    /**
     * Get downloaded base files of the repo
     */
    fun getLocalBases(repository: Repository?, dto: LocalFileDto): List<BaseBranchInfoDto> {
        return if (repository != null) basesToDto(baseFileRecordRepository.findAllByRepo(repository)
            .filter {
                ((dto.branch == null) || (it.branch == dto.branch))
                        && ((dto.fileName == null) || (it.fileName == dto.fileName))
            })
        else emptyList()
    }

    /**
     * Get downloaded solution files of the repo
     */
    fun getLocalSolutions(repository: Repository?): List<SolutionBranchInfoDto> {
        return if (repository != null)
            solutionsToDto(solutionFileRecordRepository.findAllByRepo(repository))
        else
            emptyList()
    }

    /**
     * Get downloaded solution files of the repo
     */
    fun getLocalSolutions(repository: Repository?, dto: LocalFileDto): List<SolutionBranchInfoDto> {
        return if (repository != null) solutionsToDto(solutionFileRecordRepository.findAllByRepo(repository)
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
    fun getPullRequests(repository: Repository?) = repository?.pullRequests?.map { PullRequestDto(it) }

    //    @PostMapping("/repositories/{id}/bases/delete")
    private fun deleteBaseFiles(repository: Repository?, ids: List<Long>) {
        if (repository == null) return
        val bases = baseFileRecordRepository.findAllById(ids)
        bases.map { it.branch }.toSet().forEach { branchName ->
            val branch = branchRepository.findByRepositoryAndName(repository, branchName)
            if (branch != null) branchRepository.delete(branch)
        }
        bases.forEach {
            sourceCodeStorage.deleteBaseFile(repository, it.branch, it.fileName)
        }
        baseFileRecordRepository.deleteAll(bases)
    }

    //    @PostMapping("/repositories/{id}/solutions/delete")
    private fun deleteSolutionFiles(repository: Repository?, ids: List<Long>) {
        if (repository == null) return
        val solutions = solutionFileRecordRepository.findAllById(ids)
        solutions.forEach {
            sourceCodeStorage.deleteSolutionFile(
                repository,
                it.pullRequest.sourceBranchName,
                it.pullRequest.creatorName,
                it.fileName
            )
        }
        solutionFileRecordRepository.deleteAll(solutions)
    }

    /**
     * Delete all [BaseFileRecord]s and corresponding files of the repo
     */
    fun deleteAllBaseFiles(repository: Repository?) {
        if (repository == null) return
        notificationService.notify("Started deletion of base files from repo ${repository.name}")
        repository.branches.forEach { branch ->
            sourceCodeStorage.deleteAllBaseFiles(repository, branch.name)
        }
        branchRepository.deleteAll(repository.branches)
        baseFileRecordRepository.deleteAllByRepo(repository)
        notificationService.notify("Deleted all base files of repo ${repository.name}")
    }

    /**
     * Delete all [SolutionFileRecord]s, pull request records and corresponding files of the repo
     */
    fun deleteAllSolutionFiles(repository: Repository?) {
        if (repository == null) return
        notificationService.notify("Started deletion of solution files from repo ${repository.name}")
        repository.pullRequests.forEach { pullRequest ->
            solutionFileRecordRepository.deleteAllByPullRequest(pullRequest)
            sourceCodeStorage.deleteAllSolutionFiles(repository, pullRequest.sourceBranchName, pullRequest.creatorName)
        }
        pullRequestRepository.deleteAll(repository.pullRequests)
        notificationService.notify("Deleted all solution files of repo ${repository.name}")
    }
}
