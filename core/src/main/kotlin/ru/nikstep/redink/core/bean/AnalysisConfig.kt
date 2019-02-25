package ru.nikstep.redink.core.bean

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.task.TaskExecutor
import ru.nikstep.redink.analysis.AnalysisScheduler
import ru.nikstep.redink.analysis.AnalysisService
import ru.nikstep.redink.analysis.MossAnalysisService
import ru.nikstep.redink.analysis.loader.BitbucketServiceLoader
import ru.nikstep.redink.analysis.loader.GithubServiceLoader
import ru.nikstep.redink.analysis.solutions.FileSystemSolutionStorageService
import ru.nikstep.redink.analysis.solutions.SolutionStorageService
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.model.data.AnalysisResultRepository
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.util.auth.AuthorizationService

@Configuration
class AnalysisConfig {

    @Bean
    fun solutionStorageService(
        sourceCodeRepository: SourceCodeRepository,
        repositoryRepository: RepositoryRepository
    ): SolutionStorageService {
        return FileSystemSolutionStorageService(sourceCodeRepository, repositoryRepository)
    }

    @Bean
    fun analysisService(
        solutionStorageService: SolutionStorageService,
        env: Environment
    ): MossAnalysisService {
        val mossId = env.getProperty("MOSS_ID")!!
        return MossAnalysisService(
            solutionStorageService,
            mossId
        )
    }

    @Bean
    fun analysisScheduler(
        pullRequestRepository: PullRequestRepository,
        analysisService: AnalysisService,
        analysisResultRepository: AnalysisResultRepository,
        analysisStatusCheckService: AnalysisStatusCheckService,
        githubServiceLoader: GithubServiceLoader,
        bitbucketServiceLoader: BitbucketServiceLoader,
        @Qualifier("analysisThreadPoolTaskExecutor") taskExecutor: TaskExecutor
    ): AnalysisScheduler {
        return AnalysisScheduler(
            pullRequestRepository,
            analysisService,
            analysisResultRepository,
            analysisStatusCheckService,
            githubServiceLoader,
            bitbucketServiceLoader,
            taskExecutor
        )
    }

    @Bean
    fun githubServiceLoader(
        solutionStorageService: SolutionStorageService,
        repositoryRepository: RepositoryRepository,
        authorizationService: AuthorizationService
    ): GithubServiceLoader {
        return GithubServiceLoader(solutionStorageService, repositoryRepository, authorizationService)
    }

    @Bean
    fun bitbucketServiceLoader(
        solutionStorageService: SolutionStorageService,
        repositoryRepository: RepositoryRepository
    ): BitbucketServiceLoader {
        return BitbucketServiceLoader(solutionStorageService, repositoryRepository)
    }

}