package io.gitplag.core.rest

import io.gitplag.model.dto.AnalysisDto
import io.gitplag.model.dto.InputRepositoryDto
import io.gitplag.model.dto.LocalFileDto
import io.gitplag.model.entity.BaseFileRecord
import io.gitplag.model.entity.SolutionFileRecord
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.RepositoryDataManager
import mu.KotlinLogging
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for repositories
 */
@RestController
@RequestMapping("/api/repositories/{service}/{user}/{repo}")
class RepositoryByNameController(
    private val repositoryDataManager: RepositoryDataManager,
    private val repositoryCommonController: RepositoryCommonController
) {
    private val logger = KotlinLogging.logger {}

    private fun getRepoEntity(service: String, user: String, repo: String) =
        repositoryDataManager.findByGitServiceAndName(GitProperty.valueOf(service.toUpperCase()), "$user/$repo")

    /**
     * Get the repo
     */
    @GetMapping
    fun getRepo(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String
    ) = repositoryCommonController.getRepo(getRepoEntity(service, user, repo))

    /**
     * Get analyzes of the repo
     */
    @GetMapping("/analyzes")
    fun getRepositoryAnalyzes(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String
    ) = repositoryCommonController.getRepositoryAnalyzes(getRepoEntity(service, user, repo))

    /**
     * Initiate the analysis
     */
    @PostMapping("/analyze")
    fun analyze(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String,
        @RequestBody dto: AnalysisDto
    ) = repositoryCommonController.analyze(getRepoEntity(service, user, repo), dto)

    /**
     * Initiate the analysis async
     */
    @PostMapping("/analyze/detached")
    fun analyzeDetached(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String,
        @RequestBody dto: AnalysisDto
    ) = repositoryCommonController.analyzeDetached(getRepoEntity(service, user, repo), dto)

    /**
     * Update the repo
     */
    @PutMapping("")
    fun editRepo(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String,
        @RequestBody dto: InputRepositoryDto
    ) = repositoryCommonController.editRepo(getRepoEntity(service, user, repo), dto)

    /**
     * Create a repo
     */
    @DeleteMapping
    fun deleteRepo(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String
    ) = repositoryCommonController.deleteRepo(getRepoEntity(service, user, repo))

    /**
     * Trigger download of files of the repo
     */
    @GetMapping("/files/update")
    fun updateFilesOfRepo(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String
    ) = repositoryCommonController.updateFilesOfRepo(getRepoEntity(service, user, repo))

    /**
     * Trigger download of files of the repo
     */
    @GetMapping("/files/update/detached")
    fun updateFilesOfRepoAsync(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String
    ) = repositoryCommonController.updateFilesOfRepoAsync(getRepoEntity(service, user, repo))

    /**
     * Get downloaded base and solution files of the repo
     */
    @GetMapping("/files")
    fun getFilesOfRepo(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String
    ) = repositoryCommonController.getFilesOfRepo(getRepoEntity(service, user, repo))

    /**
     * Get downloaded base files of the repo
     */
    @GetMapping("/bases")
    fun getLocalBases(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String
    ) = repositoryCommonController.getLocalBases(getRepoEntity(service, user, repo))

    /**
     * Get downloaded base files of the repo
     */
    @PostMapping("/bases")
    fun getLocalBases(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String, @RequestBody dto: LocalFileDto
    ) = repositoryCommonController.getLocalBases(getRepoEntity(service, user, repo), dto)

    /**
     * Get downloaded solution files of the repo
     */
    @GetMapping("/solutions")
    fun getLocalSolutions(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String
    ) = repositoryCommonController.getLocalSolutions(getRepoEntity(service, user, repo))

    /**
     * Get downloaded solution files of the repo
     */
    @PostMapping("/solutions")
    fun getLocalSolutions(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String,
        @RequestBody dto: LocalFileDto
    ) = repositoryCommonController.getLocalSolutions(getRepoEntity(service, user, repo), dto)

    /**
     * Get pull requests of the repo
     */
    @GetMapping("/pulls")
    fun getPullRequests(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String
    ) = repositoryCommonController.getPullRequests(getRepoEntity(service, user, repo))

    /**
     * Delete all [BaseFileRecord]s and corresponding files of the repo
     */
    @DeleteMapping("/bases/delete")
    fun deleteAllBaseFiles(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String
    ) = repositoryCommonController.deleteAllBaseFiles(getRepoEntity(service, user, repo))

    /**
     * Delete all [SolutionFileRecord]s, pull request records and corresponding files of the repo
     */
    @DeleteMapping("/solutions/delete")
    fun deleteAllSolutionFiles(
        @PathVariable service: String,
        @PathVariable user: String,
        @PathVariable repo: String
    ) = repositoryCommonController.deleteAllSolutionFiles(getRepoEntity(service, user, repo))
}
