package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.checks.github.AnalysisStatusCheckService
import ru.nikstep.redink.git.integration.GithubIntegrationService
import ru.nikstep.redink.git.webhook.BitbucketWebhookService
import ru.nikstep.redink.git.webhook.GithubWebhookService
import ru.nikstep.redink.git.webhook.GitlabWebhookService
import ru.nikstep.redink.model.data.AnalysisResultRepository
import ru.nikstep.redink.model.repo.AnalysisPairLinesRepository
import ru.nikstep.redink.model.repo.AnalysisPairRepository
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.UserRepository

@Configuration
class GitConfig {

    @Bean
    fun githubPullRequestWebhookService(
        analysisStatusCheckService: AnalysisStatusCheckService,
        pullRequestRepository: PullRequestRepository
    ): GithubWebhookService {
        return GithubWebhookService(
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