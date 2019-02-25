package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.github.BitbucketPullRequestWebhookService
import ru.nikstep.redink.github.GithubIntegrationService
import ru.nikstep.redink.github.GithubPullRequestWebhookService
import ru.nikstep.redink.github.IntegrationService
import ru.nikstep.redink.model.data.AnalysisResultRepository
import ru.nikstep.redink.model.repo.AnalysisPairLinesRepository
import ru.nikstep.redink.model.repo.AnalysisPairRepository
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.UserRepository
import ru.nikstep.redink.util.auth.AuthorizationService

@Configuration
class GitConfig {

    @Bean
    fun pullRequestService(
        authorizationService: AuthorizationService,
        analysisStatusCheckService: AnalysisStatusCheckService,
        pullRequestRepository: PullRequestRepository
    ): GithubPullRequestWebhookService {
        return GithubPullRequestWebhookService(
            authorizationService,
            analysisStatusCheckService,
            pullRequestRepository
        )
    }

    @Bean
    fun integrationService(
        userRepository: UserRepository,
        repositoryRepository: RepositoryRepository
    ): IntegrationService {
        return GithubIntegrationService(userRepository, repositoryRepository)
    }

    @Bean
    fun analysisResultRepository(
        analysisPairRepository: AnalysisPairRepository,
        analysisPairLinesRepository: AnalysisPairLinesRepository
    ): AnalysisResultRepository {
        return AnalysisResultRepository(
            analysisPairRepository,
            analysisPairLinesRepository
        )
    }

    @Bean
    fun bitbucketPullRequestWebhookService(pullRequestRepository: PullRequestRepository): BitbucketPullRequestWebhookService {
        return BitbucketPullRequestWebhookService(pullRequestRepository)
    }
}