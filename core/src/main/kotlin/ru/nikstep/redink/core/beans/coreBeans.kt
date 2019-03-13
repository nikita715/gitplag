package ru.nikstep.redink.core.beans

import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import org.springframework.context.support.beans
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import ru.nikstep.redink.checks.github.GithubAnalysisStatusCheckService
import ru.nikstep.redink.core.graphql.AnalysisQueries
import ru.nikstep.redink.core.graphql.LocalDateTimeScalarType
import ru.nikstep.redink.core.util.TokenCacheManager
import ru.nikstep.redink.model.manager.AnalysisResultDataManager
import ru.nikstep.redink.model.manager.JPlagReportDataManager
import ru.nikstep.redink.model.manager.RepositoryDataManager
import ru.nikstep.redink.util.RandomGenerator
import ru.nikstep.redink.util.auth.GithubAuthorizationService

val coreBeans = beans {
    // Data managers
    bean<RepositoryDataManager>()
    bean<AnalysisResultDataManager>()

    // Cache
    bean { TokenCacheManager("githubAccessTokens") }

    // Graphql
    bean<AnalysisQueries>()
    bean<LocalDateTimeScalarType>()

    // Github services
    bean<GithubAuthorizationService>()
    bean<GithubAnalysisStatusCheckService>()

    // Main analysis TaskExecutor
    bean<TaskExecutor>("analysisTaskExecutor") {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 4
        executor.initialize()
        executor
    }

    // Async events
    bean<ApplicationEventMulticaster>("applicationEventMulticaster") {
        val eventMulticaster = SimpleApplicationEventMulticaster()
        eventMulticaster.setTaskExecutor(ref("analysisTaskExecutor"))
        eventMulticaster
    }

    bean<RandomGenerator>()
    bean<JPlagReportDataManager>()
}