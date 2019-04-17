package io.gitplag.core.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import io.gitplag.model.dto.RepositoryDto
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import io.gitplag.model.manager.RepositoryDataManager

/**
 * Graphql repository queries
 */
class RepositoryQueries(private val repositoryDataManager: RepositoryDataManager) : GraphQLQueryResolver {

    /**
     * Get a repo by name
     */
    fun getRepo(git: GitProperty, repoFullName: String) =
        repositoryDataManager.findByGitServiceAndName(git, repoFullName)

    /**
     * Create or update a repo
     */
    fun manageRepo(
        gitService: GitProperty,
        repoFullName: String,
        language: Language?,
        filePatterns: Collection<String>?,
        analyzer: AnalyzerProperty?,
        periodicAnalysis: Boolean?,
        periodicAnalysisDelay: Int?,
        branches: List<String>?,
        analysisMode: AnalysisMode?,
        mossParameters: String?,
        jplagParameters: String?
    ): Repository {
        val repositoryDto = RepositoryDto(
            gitService, repoFullName, language, filePatterns, analyzer, periodicAnalysis,
            periodicAnalysisDelay, branches, analysisMode, mossParameters, jplagParameters
        )
        val storedRepo = repositoryDataManager.findByGitServiceAndName(gitService, repoFullName)
        return if (storedRepo == null) {
            repositoryDataManager.create(repositoryDto)
        } else {
            repositoryDataManager.update(storedRepo, repositoryDto)
        }
    }
}