package ru.nikstep.redink.core.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import ru.nikstep.redink.analysis.AnalysisRunner
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

/**
 * Configuration of analysis module
 */
@Configuration
class AnalysisConfig {

    /**
     * [FileSystemSolutionStorage] bean
     */
    @Bean
    fun solutionStorageService(
        sourceCodeRepository: SourceCodeRepository,
        repositoryRepository: RepositoryRepository
    ): FileSystemSolutionStorage = FileSystemSolutionStorage(sourceCodeRepository, repositoryRepository)


    /**
     * [MossAnalyser] bean
     */
    @Bean
    fun mossAnalyser(
        solutionStorage: SolutionStorage,
        @Value("\${redink.mossId}") mossId: String
    ): MossAnalyser = MossAnalyser(
        solutionStorage,
        mossId
    )


    /**
     * [JPlagAnalyser] bean
     */
    @Bean
    fun jplagAnalyser(
        solutionStorage: SolutionStorage,
        @Value("\${redink.solutionsDir}") solutionsDir: String
    ): JPlagAnalyser = JPlagAnalyser(solutionStorage, solutionsDir)


    /**
     * [PullRequestListener] bean
     */
    @Bean
    fun pullRequestListener(
        gitLoaders: Map<GitProperty, GitLoader>
    ): PullRequestListener = PullRequestListener(gitLoaders)


    /**
     * [GithubLoader] bean
     */
    @Bean
    fun githubLoader(
        solutionStorage: SolutionStorage,
        repositoryRepository: RepositoryRepository,
        authorizationService: AuthorizationService
    ): GithubLoader = GithubLoader(solutionStorage, repositoryRepository, authorizationService)


    /**
     * [BitbucketLoader] bean
     */
    @Bean
    fun bitbucketLoader(
        solutionStorage: SolutionStorage,
        repositoryRepository: RepositoryRepository
    ): BitbucketLoader = BitbucketLoader(solutionStorage, repositoryRepository)


    /**
     * [GitlabLoader] bean
     */
    @Bean
    fun gitlabLoader(
        solutionStorage: SolutionStorage,
        repositoryRepository: RepositoryRepository
    ): GitlabLoader = GitlabLoader(solutionStorage, repositoryRepository)


    /**
     * Map bean with all [GitLoader]s
     */
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


    /**
     * Map bean with all [Analyser]s
     */
    @Bean
    fun analysers(
        mossAnalysisService: MossAnalyser,
        jPlagAnalysisService: JPlagAnalyser
    ): Map<AnalyserProperty, Analyser> = mapOf(
        MOSS to mossAnalysisService,
        JPLAG to jPlagAnalysisService
    )


    /**
     * [TaskExecutor] bean for analysis tasks
     */
    @Bean
    fun analysisThreadPoolTaskExecutor(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 4
        executor.initialize()
        return executor
    }


    /**
     * [ApplicationEventMulticaster] bean
     */
    @Bean(name = ["applicationEventMulticaster"])
    fun simpleApplicationEventMulticaster(): ApplicationEventMulticaster {
        val eventMulticaster = SimpleApplicationEventMulticaster()
        eventMulticaster.setTaskExecutor(analysisThreadPoolTaskExecutor())
        return eventMulticaster
    }


    /**
     * [AnalysisRunner] bean
     */
    @Bean
    fun analysisManager(
        analysisStatusCheckService: AnalysisStatusCheckService,
        analysers: Map<AnalyserProperty, Analyser>,
        analysisResultDataManager: AnalysisResultDataManager
    ): AnalysisRunner = AnalysisRunner(
        analysisStatusCheckService,
        analysers,
        analysisResultDataManager
    )


}