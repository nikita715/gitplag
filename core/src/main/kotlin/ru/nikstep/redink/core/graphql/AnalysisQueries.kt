package ru.nikstep.redink.core.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.core.analysis.AnalysisAsyncRunner
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.entity.Analysis
import ru.nikstep.redink.model.enums.AnalyserProperty
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language
import ru.nikstep.redink.model.manager.RepositoryDataManager
import ru.nikstep.redink.model.repo.AnalysisRepository

/**
 * Graphql analysis requests resolver
 */
class AnalysisQueries(
    private val repositoryDataManager: RepositoryDataManager,
    private val analysisRepository: AnalysisRepository,
    private val analysisRunner: AnalysisRunner,
    private val analysisAsyncRunner: AnalysisAsyncRunner
) : GraphQLQueryResolver {

    /**
     * Get the last analysis result by the parameters
     */
    fun analysis(git: GitProperty, repoFullName: String): Analysis? =
        repositoryDataManager.findByGitServiceAndName(git, repoFullName)
            ?.let(analysisRepository::findFirstByRepositoryOrderByExecutionDateDesc)

    /**
     * Initiate the analysis
     */
    fun analyse(
        git: GitProperty, repoFullName: String, branch: String,
        analyser: AnalyserProperty?, language: Language?, mode: AnalysisMode?
    ): Analysis? {
        val repoValue = repositoryDataManager.findByGitServiceAndName(git, repoFullName) ?: return null
        return analysisRunner.run(
            AnalysisSettings(repoValue, branch, language = language, analyser = analyser, mode = mode)
        )
    }

    /**
     * Initiate the analysis
     */
    fun analyseDetached(
        git: GitProperty, repoFullName: String, branch: String, responseUrl: String,
        analyser: AnalyserProperty?, language: Language?, mode: AnalysisMode?
    ): Boolean {
        val repoValue = repositoryDataManager.findByGitServiceAndName(git, repoFullName) ?: return false
        analysisAsyncRunner.runAndRespond(
            AnalysisSettings(repoValue, branch, language = language, analyser = analyser, mode = mode), responseUrl
        )
        return true
    }
}
