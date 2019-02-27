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
import ru.nikstep.redink.analysis.solutions.FileSystemSolutionStorageService
import ru.nikstep.redink.analysis.solutions.SolutionStorageService
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.model.data.AnalysisResultRepository
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.util.Analyser
import ru.nikstep.redink.util.Analyser.JPLAG
import ru.nikstep.redink.util.Analyser.MOSS
import ru.nikstep.redink.util.Git
import ru.nikstep.redink.util.Git.BITBUCKET
import ru.nikstep.redink.util.Git.GITHUB
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
    fun mossAnalysisService(
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
    fun jplagAnalysisService(solutionStorageService: SolutionStorageService): JPlagAnalysisService {
        return JPlagAnalysisService(solutionStorageService)
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

    @Bean
    fun gitServiceLoaders(
        githubServiceLoader: GithubServiceLoader,
        bitbucketServiceLoader: BitbucketServiceLoader
    ): Map<Git, GitServiceLoader> {
        return mapOf(
            GITHUB to githubServiceLoader,
            BITBUCKET to bitbucketServiceLoader
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