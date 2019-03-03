package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import ru.nikstep.redink.checks.github.AnalysisStatusCheckService
import ru.nikstep.redink.checks.github.GithubAnalysisStatusCheckService
import ru.nikstep.redink.util.auth.AuthorizationService
import ru.nikstep.redink.util.auth.GithubAuthorizationService

@Configuration
class CoreConfig {

    @Bean
    fun authenticationService(): GithubAuthorizationService {
        return GithubAuthorizationService()
    }

    @Bean
    fun analysisResultService(authorizationService: AuthorizationService): AnalysisStatusCheckService {
        return GithubAnalysisStatusCheckService(authorizationService)
    }

    @Bean
    fun analysisThreadPoolTaskExecutor(): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 4
        executor.initialize()
        return executor
    }

}