package ru.nikstep.redink.core.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.analysis.analyser.Analyser
import ru.nikstep.redink.analysis.analyser.JPlagAnalyser
import ru.nikstep.redink.analysis.analyser.MossAnalyser
import ru.nikstep.redink.analysis.loader.BitbucketLoader
import ru.nikstep.redink.analysis.loader.GitLoader
import ru.nikstep.redink.analysis.loader.GithubLoader
import ru.nikstep.redink.analysis.loader.GitlabLoader
import ru.nikstep.redink.checks.github.AnalysisStatusCheckService
import ru.nikstep.redink.checks.github.GithubAnalysisStatusCheckService
import ru.nikstep.redink.core.analysis.AnalysisScheduler
import ru.nikstep.redink.model.data.AnalysisResultDataManager
import ru.nikstep.redink.model.repo.AnalysisPairLinesRepository
import ru.nikstep.redink.model.repo.AnalysisPairRepository
import ru.nikstep.redink.model.repo.AnalysisRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.auth.AuthorizationService
import ru.nikstep.redink.util.auth.GithubAuthorizationService

/**
 * Common application configuration
 */
@Configuration
class CoreConfig {

    /**
     * [GithubAuthorizationService] bean
     */
    @Bean
    fun githubAuthorizationService(): GithubAuthorizationService = GithubAuthorizationService()

    /**
     * [GithubAnalysisStatusCheckService] bean
     */
    @Bean
    fun githubAnalysisStatusCheckService(authorizationService: AuthorizationService): AnalysisStatusCheckService =
        GithubAnalysisStatusCheckService(authorizationService)

    /**
     * [AnalysisScheduler] bean
     */
    @Bean
    fun analysisScheduler(
        analysisRunner: AnalysisRunner,
        repositoryRepository: RepositoryRepository
    ): AnalysisScheduler =
        AnalysisScheduler(analysisRunner, repositoryRepository)

    /**
     * [AnalysisResultDataManager] bean
     */
    @Bean
    fun analysisResultDataManager(
        analysisRepository: AnalysisRepository,
        analysisPairRepository: AnalysisPairRepository,
        analysisPairLinesRepository: AnalysisPairLinesRepository
    ): AnalysisResultDataManager =
        AnalysisResultDataManager(analysisRepository, analysisPairRepository, analysisPairLinesRepository)


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
     * Map bean with all [GitLoader]s
     */
    @Bean
    fun gitServiceLoaders(
        githubServiceLoader: GithubLoader,
        bitbucketServiceLoader: BitbucketLoader,
        gitlabServiceLoader: GitlabLoader
    ): Map<GitProperty, GitLoader> = mapOf(
        GitProperty.GITHUB to githubServiceLoader,
        GitProperty.BITBUCKET to bitbucketServiceLoader,
        GitProperty.GITLAB to gitlabServiceLoader
    )


    /**
     * Map bean with all [Analyser]s
     */
    @Bean
    fun analysers(
        mossAnalysisService: MossAnalyser,
        jPlagAnalysisService: JPlagAnalyser
    ): Map<AnalyserProperty, Analyser> = mapOf(
        AnalyserProperty.MOSS to mossAnalysisService,
        AnalyserProperty.JPLAG to jPlagAnalysisService
    )
}