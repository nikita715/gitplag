package ru.nikstep.redink.core.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import ru.nikstep.redink.model.dto.RepositoryDto
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.enums.AnalyserProperty
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language
import ru.nikstep.redink.model.manager.RepositoryDataManager

/**
 * Graphql repository queries
 */
class RepositoryQueries(private val repositoryDataManager: RepositoryDataManager) : GraphQLQueryResolver {

    /**
     * Get a repo by name
     */
    fun getRepo(git: GitProperty, name: String) = repositoryDataManager.findByGitServiceAndName(git, name)

    /**
     * Create or update a repo
     */
    fun manageRepo(
        gitService: GitProperty,
        fullName: String,
        language: Language?,
        filePatterns: Collection<String>?,
        analyser: AnalyserProperty?,
        periodicAnalysis: Boolean?,
        periodicAnalysisDelay: Int?,
        branches: List<String>?,
        analysisMode: AnalysisMode?
    ): Repository {
        val repositoryDto = RepositoryDto(
            gitService, fullName, language, filePatterns, analyser, periodicAnalysis,
            periodicAnalysisDelay, branches, analysisMode
        )
        val storedRepo = repositoryDataManager.findByGitServiceAndName(gitService, fullName)
        return if (storedRepo == null) {
            repositoryDataManager.create(repositoryDto)
        } else {
            repositoryDataManager.update(storedRepo, repositoryDto)
        }
    }
}