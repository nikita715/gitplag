package io.gitplag.core.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import io.gitplag.analysis.AnalysisRunner
import io.gitplag.core.analysis.AnalysisAsyncRunner
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.entity.Analysis
import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.AnalysisRepository
import org.springframework.transaction.annotation.Transactional

/**
 * Graphql analysis requests resolver
 */
@Transactional
class AnalysisQueries(
    private val repositoryDataManager: RepositoryDataManager,
    private val analysisRepository: AnalysisRepository,
    private val analysisRunner: AnalysisRunner,
    private val analysisAsyncRunner: AnalysisAsyncRunner
) : GraphQLQueryResolver {

    /**
     * Get the last analysis result by the parameters
     */
    @Transactional
    fun analysis(git: GitProperty, repoFullName: String): Analysis? {
        val analysis = repositoryDataManager.findByGitServiceAndName(git, repoFullName)
            ?.let(analysisRepository::findFirstByRepositoryOrderByExecutionDateDesc)
        analysis?.analysisPairs?.forEach {
            it.analysisPairLines.count()
        }
        return analysis
    }

    /**
     * Initiate the analysis
     */
    @Transactional
    fun analyze(
        git: GitProperty, repoFullName: String, branch: String,
        analyzer: AnalyzerProperty?, language: Language?, mode: AnalysisMode?
    ): Analysis? {
        val repoValue = repositoryDataManager.findByGitServiceAndName(git, repoFullName) ?: return null
        return analysisRunner.run(
            AnalysisSettings(
                repoValue,
                branch,
                language = language,
                analyzer = analyzer,
                mode = mode
            )
        )
    }

    /**
     * Initiate the analysis
     */
    @Transactional
    fun analyzeDetached(
        git: GitProperty, repoFullName: String, branch: String, responseUrl: String,
        analyzer: AnalyzerProperty?, language: Language?, mode: AnalysisMode?
    ): Boolean {
        val repoValue = repositoryDataManager.findByGitServiceAndName(git, repoFullName) ?: return false
        analysisAsyncRunner.runAndRespond(
            AnalysisSettings(
                repoValue,
                branch,
                language = language,
                analyzer = analyzer,
                mode = mode
            ), responseUrl
        )
        return true
    }
}
