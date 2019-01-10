package ru.nikstep.redink.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.repo.PullRequestRepository
import ru.nikstep.redink.repo.RepositoryRepository
import ru.nikstep.redink.repo.SourceCodeRepository
import ru.nikstep.redink.repo.UserRepository
import ru.nikstep.redink.service.*

@Configuration
class BeanConfig {

    @Bean
    fun githubAppService(): GithubAppService {
        return SimpleGithubAppService()
    }

    @Bean
    fun analysisResultService(githubAppService: GithubAppService): AnalysisResultService {
        return AnalysisResultService(githubAppService)
    }

    @Bean
    fun sourceCodeService(
        sourceCodeRepository: SourceCodeRepository,
        userRepository: UserRepository,
        repositoryRepository: RepositoryRepository
    ): SourceCodeService {
        return SourceCodeService(sourceCodeRepository, userRepository, repositoryRepository)
    }

    @Bean
    fun pullRequestService(
        pullRequestRepository: PullRequestRepository,
        repositoryRepository: RepositoryRepository,
        sourceCodeService: SourceCodeService,
        githubAppService: GithubAppService,
        analysisResultService: AnalysisResultService
    ): PullRequestSavingService {
        return PullRequestSavingService(
            pullRequestRepository,
            repositoryRepository,
            sourceCodeService,
            githubAppService,
            analysisResultService
        )
    }

    @Bean
    fun plagiarismService(): PlagiarismService {
        return EmptyPlagiarismService()
    }

    @Bean
    fun pullRequestLoadingService(
        pullRequestRepository: PullRequestRepository,
        plagiarismService: PlagiarismService
    ): PullRequestLoadingService {
        return PullRequestLoadingService(pullRequestRepository, plagiarismService)
    }

}