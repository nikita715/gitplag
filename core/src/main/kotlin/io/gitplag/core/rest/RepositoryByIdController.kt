package io.gitplag.core.rest

import io.gitplag.model.dto.AnalysisDto
import io.gitplag.model.dto.InputRepositoryDto
import io.gitplag.model.dto.LocalFileDto
import io.gitplag.model.entity.BaseFileRecord
import io.gitplag.model.entity.SolutionFileRecord
import io.gitplag.model.manager.RepositoryDataManager
import org.springframework.web.bind.annotation.*

/**
 * Controller for repositories
 */
@RestController
@RequestMapping("/api/repositories/{id}")
class RepositoryByIdController(
    private val repositoryDataManager: RepositoryDataManager,
    private val repositoryCommonController: RepositoryCommonController
) {

    private fun getRepoEntity(id: Long) = repositoryDataManager.findById(id)

    /**
     * Get the repo
     */
    @GetMapping
    fun getRepo(
        @PathVariable id: Long
    ) = repositoryCommonController.getRepo(getRepoEntity(id))

    /**
     * Get analyzes of the repo
     */
    @GetMapping("/analyzes")
    fun getRepositoryAnalyzes(
        @PathVariable id: Long
    ) = repositoryCommonController.getRepositoryAnalyzes(getRepoEntity(id))

    /**
     * Initiate the analysis
     */
    @PostMapping("/analyze")
    fun analyze(
        @PathVariable id: Long,
        @RequestBody dto: AnalysisDto
    ) = repositoryCommonController.analyze(getRepoEntity(id), dto)

    /**
     * Initiate the analysis async
     */
    @PostMapping("/analyze/detached")
    fun analyzeDetached(
        @PathVariable id: Long,
        @RequestBody dto: AnalysisDto
    ) = repositoryCommonController.analyzeDetached(getRepoEntity(id), dto)

    /**
     * Update the repo
     */
    @PutMapping("")
    fun editRepo(
        @PathVariable id: Long,
        @RequestBody dto: InputRepositoryDto
    ) = repositoryCommonController.editRepo(getRepoEntity(id), dto)

    /**
     * Create a repo
     */
    @DeleteMapping
    fun deleteRepo(
        @PathVariable id: Long
    ) = repositoryCommonController.deleteRepo(getRepoEntity(id))

    /**
     * Trigger download of files of the repo
     */
    @GetMapping("/files/update")
    fun updateFilesOfRepo(
        @PathVariable id: Long
    ) = repositoryCommonController.updateFilesOfRepo(getRepoEntity(id))

    /**
     * Trigger download of files of the repo
     */
    @GetMapping("/files/update/detached")
    fun updateFilesOfRepoAsync(
        @PathVariable id: Long
    ) = repositoryCommonController.updateFilesOfRepoAsync(getRepoEntity(id))

    /**
     * Get downloaded base and solution files of the repo
     */
    @GetMapping("/files")
    fun getFilesOfRepo(
        @PathVariable id: Long
    ) = repositoryCommonController.getFilesOfRepo(getRepoEntity(id))

    /**
     * Get downloaded base files of the repo
     */
    @GetMapping("/bases")
    fun getLocalBases(
        @PathVariable id: Long
    ) = repositoryCommonController.getLocalBases(getRepoEntity(id))

    /**
     * Get downloaded base files of the repo
     */
    @PostMapping("/bases")
    fun getLocalBases(
        @PathVariable id: Long,
        @RequestBody dto: LocalFileDto
    ) = repositoryCommonController.getLocalBases(getRepoEntity(id), dto)

    /**
     * Get downloaded solution files of the repo
     */
    @GetMapping("/solutions")
    fun getLocalSolutions(
        @PathVariable id: Long
    ) = repositoryCommonController.getLocalSolutions(getRepoEntity(id))

    /**
     * Get downloaded solution files of the repo
     */
    @PostMapping("/solutions")
    fun getLocalSolutions(
        @PathVariable id: Long,
        @RequestBody dto: LocalFileDto
    ) = repositoryCommonController.getLocalSolutions(getRepoEntity(id), dto)

    /**
     * Get pull requests of the repo
     */
    @GetMapping("/pulls")
    fun getPullRequests(
        @PathVariable id: Long
    ) = repositoryCommonController.getPullRequests(getRepoEntity(id))

    /**
     * Delete all [BaseFileRecord]s and corresponding files of the repo
     */
    @DeleteMapping("/bases/delete")
    fun deleteAllBaseFiles(
        @PathVariable id: Long
    ) = repositoryCommonController.deleteAllBaseFiles(getRepoEntity(id))

    /**
     * Delete all [SolutionFileRecord]s, pull request records and corresponding files of the repo
     */
    @DeleteMapping("/solutions/delete")
    fun deleteAllSolutionFiles(
        @PathVariable id: Long
    ) = repositoryCommonController.deleteAllSolutionFiles(getRepoEntity(id))
}
