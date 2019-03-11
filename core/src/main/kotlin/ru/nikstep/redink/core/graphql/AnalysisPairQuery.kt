package ru.nikstep.redink.core.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import ru.nikstep.redink.model.entity.Analysis
import ru.nikstep.redink.model.repo.AnalysisRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.GitProperty

/**
 * Graphql analysis requests resolver
 */
@Suppress("unused")
class AnalysisPairQuery(
    private val repositoryRepository: RepositoryRepository,
    private val analysisRepository: AnalysisRepository
) : GraphQLQueryResolver {

    fun analysis(gitService: String, repo: String): Analysis? =
        repositoryRepository.findByGitServiceAndName(GitProperty.valueOf(gitService.toUpperCase()), repo)
            .let(analysisRepository::findFirstByRepositoryOrderByExecutionDateDesc)

}
