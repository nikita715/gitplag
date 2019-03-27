package ru.nikstep.redink.model.manager

import org.springframework.transaction.annotation.Transactional
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.enums.AnalyserProperty
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
     * Create repositories by [repoNames]
     */
    @Transactional
    fun create(ownerName: String, gitProperty: GitProperty, repoNames: List<String>) {
        repoNames.forEach { repoName ->
            repositoryRepository.save(
                Repository(
                    language = Language.JAVA,
                    name = repoName,
                    periodicAnalysis = false,
                    gitService = gitProperty,
                    analyser = AnalyserProperty.MOSS,
                    branches = listOf("master")
                )
            )
        }
    }

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
     * Delete repositories by [repoNames]
     */
    @Transactional
    fun delete(repoNames: List<String>) {
        repoNames.forEach { repoName ->
            repositoryRepository.deleteByGitServiceAndName(
                GitProperty.GITHUB,
                repoName
            )
        }
    }

    /**
     * Save all [repositories]
     */
    @Transactional
    fun saveAll(repositories: List<Repository>) {
        repositoryRepository.saveAll(repositories)
    }

}