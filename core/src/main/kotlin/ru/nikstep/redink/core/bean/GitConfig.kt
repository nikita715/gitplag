package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.github.BitbucketWebhookService
import ru.nikstep.redink.github.GithubIntegrationService
import ru.nikstep.redink.github.GithubPullRequestWebhookService
import ru.nikstep.redink.github.GitlabWebhookService
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
    fun githubPullRequestWebhookService(
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
    fun bitbucketPullRequestWebhookService(
        pullRequestRepository: PullRequestRepository
    ): BitbucketWebhookService {
        return BitbucketWebhookService(pullRequestRepository)
    }

    @Bean
    fun gitlabPullRequestWebhookService(
        authorizationService: AuthorizationService,
        analysisStatusCheckService: AnalysisStatusCheckService,
        pullRequestRepository: PullRequestRepository
    ): GitlabWebhookService {
        return GitlabWebhookService(pullRequestRepository)
    }

    @Bean
    fun githubIntegrationService(
        userRepository: UserRepository,
        repositoryRepository: RepositoryRepository
    ): GithubIntegrationService {
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
}