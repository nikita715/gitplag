package ru.nikstep.redink.core.beans

import org.springframework.context.event.SimpleApplicationEventMulticaster
import org.springframework.context.support.beans
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.analysis.PullRequestListener
import ru.nikstep.redink.analysis.solutions.FileSystemSolutionStorage
import ru.nikstep.redink.checks.github.GithubAnalysisStatusCheckService
import ru.nikstep.redink.core.analysis.AnalysisScheduler
import ru.nikstep.redink.core.graphql.AnalysisQueries
import ru.nikstep.redink.core.graphql.LocalDateTimeScalarType
import ru.nikstep.redink.core.util.TokenCacheManager
import ru.nikstep.redink.model.manager.AnalysisResultDataManager
import ru.nikstep.redink.model.manager.RepositoryDataManager
import ru.nikstep.redink.util.auth.GithubAuthorizationService

val coreBeans = beans {
    bean<GithubAuthorizationService>()
    bean<FileSystemSolutionStorage>()
    bean { PullRequestListener(ref("gitLoaders")) }
    bean<AnalysisResultDataManager>()
    bean<AnalysisScheduler>()
    bean<GithubAnalysisStatusCheckService>()
    bean { AnalysisRunner(ref(), ref("analysers"), ref()) }
    bean<RepositoryDataManager>()
    bean { TokenCacheManager("githubAccessTokens") }

    // Graphql
    bean<AnalysisQueries>()
    bean<LocalDateTimeScalarType>()

    bean("analysisTaskExecutor") {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 4
        executor.initialize()
        executor
    }
    bean("applicationEventMulticaster") {
        val eventMulticaster = SimpleApplicationEventMulticaster()
        eventMulticaster.setTaskExecutor(ref("analysisTaskExecutor"))
        eventMulticaster
    }
}