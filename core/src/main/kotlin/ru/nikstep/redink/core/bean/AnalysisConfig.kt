package ru.nikstep.redink.core.bean

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import ru.nikstep.redink.analysis.AnalysisManager
import ru.nikstep.redink.analysis.PullRequestListener
import ru.nikstep.redink.analysis.analyser.Analyser
import ru.nikstep.redink.analysis.analyser.JPlagAnalyser
import ru.nikstep.redink.analysis.analyser.MossAnalyser
import ru.nikstep.redink.analysis.loader.BitbucketLoader
import ru.nikstep.redink.analysis.loader.GitLoader
import ru.nikstep.redink.analysis.loader.GithubLoader
import ru.nikstep.redink.analysis.loader.GitlabLoader
import ru.nikstep.redink.analysis.solutions.FileSystemSolutionStorage
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.checks.github.AnalysisStatusCheckService
import ru.nikstep.redink.model.data.AnalysisResultDataManager
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.AnalyserProperty.JPLAG
import ru.nikstep.redink.util.AnalyserProperty.MOSS
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.GitProperty.*
import ru.nikstep.redink.util.auth.AuthorizationService

@Configuration
class AnalysisConfig {

    @Bean
    fun solutionStorageService(
        sourceCodeRepository: SourceCodeRepository,
        repositoryRepository: RepositoryRepository
    ): SolutionStorage = FileSystemSolutionStorage(sourceCodeRepository, repositoryRepository)


    @Bean
    fun mossAnalysisService(
        solutionStorage: SolutionStorage,
        @Value("\${redink.mossId}") mossId: String
    ): MossAnalyser = MossAnalyser(
        solutionStorage,
        mossId
    )

    @Bean
    fun jplagAnalysisService(
        solutionStorage: SolutionStorage,
        @Value("\${redink.solutionsDir}") solutionsDir: String
    ): JPlagAnalyser = JPlagAnalyser(solutionStorage, solutionsDir)


    @Bean
    fun pullRequestListener(
        gitLoaders: Map<GitProperty, GitLoader>
    ): PullRequestListener = PullRequestListener(gitLoaders)


    @Bean
    fun githubServiceLoader(
        solutionStorage: SolutionStorage,
        repositoryRepository: RepositoryRepository,
        authorizationService: AuthorizationService
    ): GithubLoader = GithubLoader(solutionStorage, repositoryRepository, authorizationService)


    @Bean
    fun bitbucketServiceLoader(
        solutionStorage: SolutionStorage,
        repositoryRepository: RepositoryRepository
    ): BitbucketLoader = BitbucketLoader(solutionStorage, repositoryRepository)


    @Bean
    fun gitlabServiceLoader(
        solutionStorage: SolutionStorage,
        repositoryRepository: RepositoryRepository
    ): GitlabLoader = GitlabLoader(solutionStorage, repositoryRepository)


    @Bean
    fun gitServiceLoaders(
        githubServiceLoader: GithubLoader,
        bitbucketServiceLoader: BitbucketLoader,
        gitlabServiceLoader: GitlabLoader
    ): Map<GitProperty, GitLoader> = mapOf(
        GITHUB to githubServiceLoader,
        BITBUCKET to bitbucketServiceLoader,
        GITLAB to gitlabServiceLoader
    )


    @Bean
    fun analysers(
        mossAnalysisService: MossAnalyser,
        jPlagAnalysisService: JPlagAnalyser
    ): Map<AnalyserProperty, Analyser> = mapOf(
        MOSS to mossAnalysisService,
        JPLAG to jPlagAnalysisService
    )


    @Bean
    fun analysisThreadPoolTaskExecutor(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 4
        executor.initialize()
        return executor
    }

    @Bean(name = ["applicationEventMulticaster"])
    fun simpleApplicationEventMulticaster(): ApplicationEventMulticaster {
        val eventMulticaster = SimpleApplicationEventMulticaster()
        eventMulticaster.setTaskExecutor(analysisThreadPoolTaskExecutor())
        return eventMulticaster
    }

    @Bean
    fun analysisManager(
        analysisStatusCheckService: AnalysisStatusCheckService,
        analysers: Map<AnalyserProperty, Analyser>,
        analysisResultDataManager: AnalysisResultDataManager
    ): AnalysisManager = AnalysisManager(
        analysisStatusCheckService,
        analysers,
        analysisResultDataManager
    )


}