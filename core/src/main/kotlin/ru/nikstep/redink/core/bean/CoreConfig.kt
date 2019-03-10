package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.analysis.AnalysisManager
import ru.nikstep.redink.checks.github.AnalysisStatusCheckService
import ru.nikstep.redink.checks.github.GithubAnalysisStatusCheckService
import ru.nikstep.redink.core.AnalysisScheduler
import ru.nikstep.redink.model.data.AnalysisResultRepository
import ru.nikstep.redink.model.repo.AnalysisPairLinesRepository
import ru.nikstep.redink.model.repo.AnalysisPairRepository
import ru.nikstep.redink.model.repo.AnalysisRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.auth.AuthorizationService
import ru.nikstep.redink.util.auth.GithubAuthorizationService

@Configuration
class CoreConfig {

    @Bean
    fun authenticationService(): GithubAuthorizationService {
        return GithubAuthorizationService()
    }

    @Bean
    fun analysisResultService(authorizationService: AuthorizationService): AnalysisStatusCheckService {
        return GithubAnalysisStatusCheckService(authorizationService)
    }

    @Bean
    fun analysisScheduler(
        analysisManager: AnalysisManager,
        repositoryRepository: RepositoryRepository
    ): AnalysisScheduler {
        return AnalysisScheduler(analysisManager, repositoryRepository)
    }

    @Bean
    fun analysisResultRepository(
        analysisRepository: AnalysisRepository,
        analysisPairRepository: AnalysisPairRepository,
        analysisPairLinesRepository: AnalysisPairLinesRepository
    ): AnalysisResultRepository {
        return AnalysisResultRepository(analysisRepository, analysisPairRepository, analysisPairLinesRepository)
    }

}