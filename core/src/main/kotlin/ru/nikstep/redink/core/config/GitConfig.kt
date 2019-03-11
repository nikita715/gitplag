package ru.nikstep.redink.core.config

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.checks.github.AnalysisStatusCheckService
import ru.nikstep.redink.git.integration.GithubIntegrationService
import ru.nikstep.redink.git.webhook.BitbucketWebhookService
import ru.nikstep.redink.git.webhook.GithubWebhookService
import ru.nikstep.redink.git.webhook.GitlabWebhookService
import ru.nikstep.redink.model.manager.RepositoryDataManager
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.UserRepository

/**
 * Configuration of git module
 */
@Configuration
class GitConfig {

    /**
     * [GithubWebhookService] bean
     */
    @Bean
    fun githubWebhookService(
        analysisStatusCheckService: AnalysisStatusCheckService,
        pullRequestRepository: PullRequestRepository,
        applicationEventPublisher: ApplicationEventPublisher
    ): GithubWebhookService {
        return GithubWebhookService(
            analysisStatusCheckService,
            pullRequestRepository,
            applicationEventPublisher
        )
    }

    /**
     * [BitbucketWebhookService] bean
     */
    @Bean
    fun bitbucketWebhookService(
        pullRequestRepository: PullRequestRepository,
        applicationEventPublisher: ApplicationEventPublisher
    ): BitbucketWebhookService {
        return BitbucketWebhookService(pullRequestRepository, applicationEventPublisher)
    }


    /**
     * [GitlabWebhookService] bean
     */
    @Bean
    fun gitlabWebhookService(
        pullRequestRepository: PullRequestRepository,
        applicationEventPublisher: ApplicationEventPublisher
    ): GitlabWebhookService {
        return GitlabWebhookService(pullRequestRepository, applicationEventPublisher)
    }


    /**
     * [GithubIntegrationService] bean
     */
    @Bean
    fun githubIntegrationService(
        userRepository: UserRepository,
        repositoryDataManager: RepositoryDataManager
    ): GithubIntegrationService {
        return GithubIntegrationService(userRepository, repositoryDataManager)
    }


    /**
     * [RepositoryDataManager] bean
     */
    @Bean
    fun repositoryDataManager(
        repositoryRepository: RepositoryRepository,
        userRepository: UserRepository
    ): RepositoryDataManager =
        RepositoryDataManager(repositoryRepository, userRepository)
}