package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.github.IntegrationService
import ru.nikstep.redink.github.PullRequestWebhookService
import ru.nikstep.redink.model.repo.AnalysisPairLinesRepository
import ru.nikstep.redink.model.repo.AnalysisPairRepository
import ru.nikstep.redink.model.repo.AnalysisResultRepository
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.UserRepository
import ru.nikstep.redink.util.auth.AuthorizationService

@Configuration
open class GithubConfig {

    @Bean
    open fun pullRequestService(
        authorizationService: AuthorizationService,
        analysisStatusCheckService: AnalysisStatusCheckService,
        pullRequestRepository: PullRequestRepository
    ): PullRequestWebhookService {
        return PullRequestWebhookService(
            authorizationService,
            analysisStatusCheckService,
            pullRequestRepository
        )
    }

    @Bean
    open fun integrationService(
        userRepository: UserRepository,
        repositoryRepository: RepositoryRepository
    ): IntegrationService {
        return IntegrationService(userRepository, repositoryRepository)
    }

    @Bean
    open fun analysisResultRepository(
        analysisPairRepository: AnalysisPairRepository,
        analysisPairLinesRepository: AnalysisPairLinesRepository
    ): AnalysisResultRepository {
        return AnalysisResultRepository(analysisPairRepository, analysisPairLinesRepository)
    }
}