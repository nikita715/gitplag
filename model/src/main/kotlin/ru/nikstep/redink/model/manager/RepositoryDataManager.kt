package ru.nikstep.redink.model.manager

import org.springframework.transaction.annotation.Transactional
import ru.nikstep.redink.model.dto.RepositoryDto
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.enums.AnalyserProperty
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language
import ru.nikstep.redink.model.repo.RepositoryRepository

/**
 * Data manager of [Repository]
 */
@Transactional
class RepositoryDataManager(
    private val repositoryRepository: RepositoryRepository
) {

    /**
     * Create a repositoriy
     */
    @Transactional
    fun create(repositoryDto: RepositoryDto): Repository =
        repositoryRepository.save(
            Repository(
                name = repositoryDto.fullName,
                gitService = repositoryDto.gitService,
                language = repositoryDto.language ?: Language.JAVA,
                filePatterns = repositoryDto.filePatterns ?: emptyList(),
                analyser = repositoryDto.analyser ?: AnalyserProperty.MOSS,
                periodicAnalysis = repositoryDto.periodicAnalysis ?: false,
                periodicAnalysisDelay = repositoryDto.periodicAnalysisDelay ?: 10,
                branches = repositoryDto.branches ?: emptyList(),
                analysisMode = repositoryDto.analysisMode ?: AnalysisMode.PAIRS,
                mossParameters = repositoryDto.mossParameters ?: "",
                jplagParameters = repositoryDto.jplagParameters ?: ""
            )
        )

    /**
     * Update the [repo] by the [repositoryDto]
     */
    @Transactional
    fun update(repo: Repository, repositoryDto: RepositoryDto): Repository =
        repositoryRepository.save(
            repo.copy(
                language = repositoryDto.language ?: repo.language,
                filePatterns = repositoryDto.filePatterns ?: repo.filePatterns,
                analyser = repositoryDto.analyser ?: repo.analyser,
                periodicAnalysis = repositoryDto.periodicAnalysis ?: repo.periodicAnalysis,
                periodicAnalysisDelay = repositoryDto.periodicAnalysisDelay ?: repo.periodicAnalysisDelay,
                branches = repositoryDto.branches ?: repo.branches,
                analysisMode = repositoryDto.analysisMode ?: repo.analysisMode,
                mossParameters = repositoryDto.mossParameters ?: repo.mossParameters,
                jplagParameters = repositoryDto.jplagParameters ?: repo.jplagParameters
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
     * Save the [repo]
     */
    @Transactional
    fun save(repo: Repository): Repository = repositoryRepository.save(repo)

    /**
     * Find repositories that must be analysed at this time
     */
    @Transactional(readOnly = true)
    fun findRequiredToAnalyse(): List<Repository> = repositoryRepository.findRequiredToAnalyse()

    /**
     * Get file name regexps of the [repo]
     */
    @Transactional(readOnly = true)
    fun findFileNameRegexps(repo: Repository) = repositoryRepository.findById(repo.id)
        .let { if (it.isPresent) it.get().filePatterns else emptyList() }.also { it.size }

    @Transactional(readOnly = true)
    fun findAll() = repositoryRepository.findAll()
}
