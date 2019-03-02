package ru.nikstep.redink.core.bean

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.task.TaskExecutor
import ru.nikstep.redink.analysis.AnalysisScheduler
import ru.nikstep.redink.analysis.analyser.Analyser
import ru.nikstep.redink.analysis.analyser.JPlagAnalyser
import ru.nikstep.redink.analysis.analyser.MossAnalyser
import ru.nikstep.redink.analysis.loader.BitbucketLoader
import ru.nikstep.redink.analysis.loader.GitLoader
import ru.nikstep.redink.analysis.loader.GithubLoader
import ru.nikstep.redink.analysis.loader.GitlabLoader
import ru.nikstep.redink.analysis.solutions.FileSystemSolutionStorage
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.model.data.AnalysisResultRepository
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.AnalyserProperty.JPLAG
import ru.nikstep.redink.util.AnalyserProperty.MOSS
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.GitProperty.*
import ru.nikstep.redink.util.asPathInRoot
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
    ): MossAnalyser {
        val mossId = env.getProperty("MOSS_ID")!!
        return MossAnalyser(
            solutionStorage,
            mossId
        )
    }

    @Bean
    fun jplagAnalysisService(solutionStorage: SolutionStorage): JPlagAnalyser {
        return JPlagAnalyser(solutionStorage, "solutions".asPathInRoot())
    }

    @Bean
    fun analysisScheduler(
        pullRequestRepository: PullRequestRepository,
        analysisResultRepository: AnalysisResultRepository,
        analysisStatusCheckService: AnalysisStatusCheckService,
        @Qualifier("analysisThreadPoolTaskExecutor") taskExecutor: TaskExecutor,
        gitLoaders: Map<GitProperty, GitLoader>,
        analysers: Map<AnalyserProperty, Analyser>
    ): AnalysisScheduler {
        return AnalysisScheduler(
            pullRequestRepository,
            analysisResultRepository,
            analysisStatusCheckService,
            taskExecutor,
            gitLoaders,
            analysers
        )
    }

    @Bean
    fun githubServiceLoader(
        solutionStorage: SolutionStorage,
        repositoryRepository: RepositoryRepository,
        authorizationService: AuthorizationService
    ): GithubLoader {
        return GithubLoader(solutionStorage, repositoryRepository, authorizationService)
    }

    @Bean
    fun bitbucketServiceLoader(
        solutionStorage: SolutionStorage,
        repositoryRepository: RepositoryRepository
    ): BitbucketLoader {
        return BitbucketLoader(solutionStorage, repositoryRepository)
    }

    @Bean
    fun gitlabServiceLoader(
        solutionStorage: SolutionStorage,
        repositoryRepository: RepositoryRepository
    ): GitlabLoader {
        return GitlabLoader(solutionStorage, repositoryRepository)
    }

    @Bean
    fun gitServiceLoaders(
        githubServiceLoader: GithubLoader,
        bitbucketServiceLoader: BitbucketLoader,
        gitlabServiceLoader: GitlabLoader
    ): Map<GitProperty, GitLoader> {
        return mapOf(
            GITHUB to githubServiceLoader,
            BITBUCKET to bitbucketServiceLoader,
            GITLAB to gitlabServiceLoader
        )
    }

    @Bean
    fun analysers(
        mossAnalysisService: MossAnalyser,
        jPlagAnalysisService: JPlagAnalyser
    ): Map<AnalyserProperty, Analyser> {
        return mapOf(
            MOSS to mossAnalysisService,
            JPLAG to jPlagAnalysisService
        )
    }

}