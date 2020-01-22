package io.gitplag.model.manager

import io.gitplag.model.dto.InputRepositoryDto
import io.gitplag.model.dto.RepositoryUpdate
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.repo.RepositoryRepository
import org.springframework.transaction.annotation.Transactional

/**
 * Data manager of [Repository]
 */
@Transactional
class RepositoryDataManager(
    private val repositoryRepository: RepositoryRepository
) {

    /**
     * Create a repository
     */
    @Transactional
    fun create(gitId: String, repositoryDto: InputRepositoryDto): Repository =
        repositoryRepository.save(
            Repository(
                name = repositoryDto.name,
                gitId = gitId,
                gitService = repositoryDto.git,
                language = repositoryDto.language,
                filePatterns = repositoryDto.filePatterns,
                analyzer = repositoryDto.analyzer ?: AnalyzerProperty.MOSS,
                analysisMode = repositoryDto.analysisMode ?: AnalysisMode.PAIRS,
                autoCloningEnabled = repositoryDto.autoCloningEnabled ?: true
            )
        )

    /**
     * Update the [repo] by the [repositoryDto]
     */
    @Transactional
    fun update(repo: Repository, repositoryDto: RepositoryUpdate): Repository =
        repositoryRepository.save(
            repo.copy(
                language = repositoryDto.language ?: repo.language,
                filePatterns = repositoryDto.filePatterns,
                analyzer = repositoryDto.analyzer ?: repo.analyzer,
                analysisMode = repositoryDto.analysisMode ?: repo.analysisMode,
                autoCloningEnabled = repositoryDto.autoCloningEnabled ?: repo.autoCloningEnabled
            )
        )

    /**
     * Check that the file name matches with any of the repo file name patterns
     */
    @Transactional(readOnly = true)
    fun nameMatchesRegexp(fileName: String, repo: Repository): Boolean {
        repo.filePatterns.forEach {
            if (it.toRegex().matches(fileName)) return true
        }
        return false
    }

    /**
     * Find a repo by [gitService] and [name]
     */
    @Transactional(readOnly = true)
    fun findByGitServiceAndName(gitService: GitProperty, name: String): Repository? =
        repositoryRepository.findByGitServiceAndName(gitService, name)

    /**
     * Find a repo by [repoId]
     */
    @Transactional(readOnly = true)
    fun findById(repoId: Long): Repository? =
        repositoryRepository.findById(repoId).orElse(null)

    /**
     * Find repos by [repoIds]
     */
    @Transactional(readOnly = true)
    fun findById(repoIds: List<Long>): List<Repository> =
        repositoryRepository.findAllById(repoIds)

    /**
     * Save the [repo]
     */
    @Transactional
    fun save(repo: Repository): Repository = repositoryRepository.save(repo)

    /**
     * Get file name regexps of the [repo]
     */
    @Transactional(readOnly = true)
    fun findFileNameRegexps(repo: Repository) = repositoryRepository.findById(repo.id)
        .let { if (it.isPresent) it.get().filePatterns else emptyList() }.also { it.size }

    /**
     * Get all repositories
     */
    @Transactional(readOnly = true)
    fun findAll() = repositoryRepository.findAll()

    /**
     * Delete the [repo]
     */
    @Transactional
    fun delete(repo: Repository) = repositoryRepository.delete(repo)
}
