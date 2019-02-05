package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.checks.GithubAnalysisStatusCheckService
import ru.nikstep.redink.util.auth.AuthorizationService
import ru.nikstep.redink.util.auth.GithubAuthorizationService

@Configuration
open class CoreConfig {

    @Bean
    open fun authenticationService(): GithubAuthorizationService {
        return GithubAuthorizationService()
    }

    @Bean
    open fun analysisResultService(authorizationService: AuthorizationService): AnalysisStatusCheckService {
        return GithubAnalysisStatusCheckService(authorizationService)
    }

}