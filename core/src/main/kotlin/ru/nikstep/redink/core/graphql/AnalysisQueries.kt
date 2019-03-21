package ru.nikstep.redink.core.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.analyser
import ru.nikstep.redink.model.data.language
import ru.nikstep.redink.model.data.mode
import ru.nikstep.redink.model.entity.Analysis
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.repo.AnalysisRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.RepositoryNotFoundException

/**
 * Graphql analysis requests resolver
 */
@Suppress("unused")
class AnalysisQueries(
    private val repositoryRepository: RepositoryRepository,
    private val analysisRepository: AnalysisRepository,
    private val analysisRunner: AnalysisRunner
) : GraphQLQueryResolver {

    fun analysis(gitService: String, repo: String): Analysis? =
        repositoryRepository.findByGitServiceAndName(GitProperty.valueOf(gitService.toUpperCase()), repo)
            ?.let(analysisRepository::findFirstByRepositoryOrderByExecutionDateDesc)

    fun analyse(
        gitService: String, repo: String, branch: String,
        analyser: String?, language: String?, mode: String?
    ): Analysis? {
        val repoValue =
            repositoryRepository.findByGitServiceAndName(GitProperty.valueOf(gitService.toUpperCase()), repo)
                ?: throw RepositoryNotFoundException()
        return analysisRunner.run(
            AnalysisSettings(repoValue, branch).language(language).analyser(analyser).mode(mode)
        )
    }
}
