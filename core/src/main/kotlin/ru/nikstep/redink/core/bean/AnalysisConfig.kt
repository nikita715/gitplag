package ru.nikstep.redink.core.bean

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.task.TaskExecutor
import ru.nikstep.redink.analysis.AnalysisScheduler
import ru.nikstep.redink.analysis.AnalysisService
import ru.nikstep.redink.analysis.JPlagAnalysisService
import ru.nikstep.redink.analysis.MossAnalysisService
import ru.nikstep.redink.analysis.loader.BitbucketServiceLoader
import ru.nikstep.redink.analysis.loader.GitServiceLoader
import ru.nikstep.redink.analysis.loader.GithubServiceLoader
import ru.nikstep.redink.analysis.loader.GitlabServiceLoader
import ru.nikstep.redink.analysis.solutions.FileSystemSolutionStorage
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.model.data.AnalysisResultRepository
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.util.Analyser
import ru.nikstep.redink.util.Analyser.JPLAG
import ru.nikstep.redink.util.Analyser.MOSS
import ru.nikstep.redink.util.Git
import ru.nikstep.redink.util.Git.*
import ru.nikstep.redink.util.auth.AuthorizationService

@Configuration
class AnalysisConfig {

    @Bean
    fun solutionStorageService(
        sourceCodeRepository: SourceCodeRepository,
        repositoryRepository: RepositoryRepository
    ): SolutionStorage {
        return FileSystemSolutionStorage(sourceCodeRepository, repositoryRepository)
    }

    @Bean
    fun mossAnalysisService(
        solutionStorage: SolutionStorage,
        env: Environment
    ): MossAnalysisService {
        val mossId = env.getProperty("MOSS_ID")!!
        return MossAnalysisService(
            solutionStorage,
            mossId
        )
    }

    @Bean
    fun jplagAnalysisService(solutionStorage: SolutionStorage): JPlagAnalysisService {
        return JPlagAnalysisService(solutionStorage)
    }

    @Bean
    fun analysisScheduler(
        pullRequestRepository: PullRequestRepository,
        analysisResultRepository: AnalysisResultRepository,
        analysisStatusCheckService: AnalysisStatusCheckService,
        @Qualifier("analysisThreadPoolTaskExecutor") taskExecutor: TaskExecutor,
        gitServiceLoaders: Map<Git, GitServiceLoader>,
        analysers: Map<Analyser, AnalysisService>
    ): AnalysisScheduler {
        return AnalysisScheduler(
            pullRequestRepository,
            analysisResultRepository,
            analysisStatusCheckService,
            taskExecutor,
            gitServiceLoaders,
            analysers
        )
    }

    @Bean
    fun githubServiceLoader(
        solutionStorage: SolutionStorage,
        repositoryRepository: RepositoryRepository,
        authorizationService: AuthorizationService
    ): GithubServiceLoader {
        return GithubServiceLoader(solutionStorage, repositoryRepository, authorizationService)
    }

    @Bean
    fun bitbucketServiceLoader(
        solutionStorage: SolutionStorage,
        repositoryRepository: RepositoryRepository
    ): BitbucketServiceLoader {
        return BitbucketServiceLoader(solutionStorage, repositoryRepository)
    }

    @Bean
    fun gitlabServiceLoader(
        solutionStorage: SolutionStorage,
        repositoryRepository: RepositoryRepository
    ): GitlabServiceLoader {
        return GitlabServiceLoader(solutionStorage, repositoryRepository)
    }

    @Bean
    fun gitServiceLoaders(
        githubServiceLoader: GithubServiceLoader,
        bitbucketServiceLoader: BitbucketServiceLoader,
        gitlabServiceLoader: GitlabServiceLoader
    ): Map<Git, GitServiceLoader> {
        return mapOf(
            GITHUB to githubServiceLoader,
            BITBUCKET to bitbucketServiceLoader,
            GITLAB to gitlabServiceLoader
        )
    }

    @Bean
    fun analysers(
        mossAnalysisService: MossAnalysisService,
        jPlagAnalysisService: JPlagAnalysisService
    ): Map<Analyser, AnalysisService> {
        return mapOf(
            MOSS to mossAnalysisService,
            JPLAG to jPlagAnalysisService
        )
    }

}