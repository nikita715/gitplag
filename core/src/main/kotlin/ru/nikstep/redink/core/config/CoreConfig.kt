package ru.nikstep.redink.core.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.checks.github.AnalysisStatusCheckService
import ru.nikstep.redink.checks.github.GithubAnalysisStatusCheckService
import ru.nikstep.redink.core.analysis.AnalysisScheduler
import ru.nikstep.redink.model.data.AnalysisResultDataManager
import ru.nikstep.redink.model.repo.AnalysisPairLinesRepository
import ru.nikstep.redink.model.repo.AnalysisPairRepository
import ru.nikstep.redink.model.repo.AnalysisRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.auth.AuthorizationService
import ru.nikstep.redink.util.auth.GithubAuthorizationService

/**
 * Common application configuration
 */
@Configuration
class CoreConfig {

    /**
     * [GithubAuthorizationService] bean
     */
    @Bean
    fun githubAuthorizationService(): GithubAuthorizationService = GithubAuthorizationService()

    /**
     * [GithubAnalysisStatusCheckService] bean
     */
    @Bean
    fun githubAnalysisStatusCheckService(authorizationService: AuthorizationService): AnalysisStatusCheckService =
        GithubAnalysisStatusCheckService(authorizationService)

    /**
     * [AnalysisScheduler] bean
     */
    @Bean
    fun analysisScheduler(
        analysisRunner: AnalysisRunner,
        repositoryRepository: RepositoryRepository
    ): AnalysisScheduler =
        AnalysisScheduler(analysisRunner, repositoryRepository)

    /**
     * [AnalysisResultDataManager] bean
     */
    @Bean
    fun analysisResultDataManager(
        analysisRepository: AnalysisRepository,
        analysisPairRepository: AnalysisPairRepository,
        analysisPairLinesRepository: AnalysisPairLinesRepository
    ): AnalysisResultDataManager =
        AnalysisResultDataManager(analysisRepository, analysisPairRepository, analysisPairLinesRepository)

}