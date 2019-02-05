package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import ru.nikstep.redink.analysis.AnalysisScheduler
import ru.nikstep.redink.analysis.AnalysisService
import ru.nikstep.redink.analysis.MossAnalysisService
import ru.nikstep.redink.analysis.solutions.FileSystemSolutionService
import ru.nikstep.redink.analysis.solutions.SolutionService
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.model.repo.AnalysisResultRepository
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.model.repo.UserRepository
import ru.nikstep.redink.util.auth.AuthorizationService

@Configuration
open class AnalysisConfig {

    @Bean
    open fun sourceCodeService(
        sourceCodeRepository: SourceCodeRepository,
        userRepository: UserRepository,
        repositoryRepository: RepositoryRepository
    ): SolutionService {
        return FileSystemSolutionService(repositoryRepository, userRepository)
    }

    @Bean
    open fun analysisService(
        solutionService: SolutionService,
        repositoryRepository: RepositoryRepository,
        authorizationService: AuthorizationService,
        env: Environment
    ): MossAnalysisService {
        val mossId = env.getProperty("MOSS_ID")
        return MossAnalysisService(
            solutionService,
            repositoryRepository,
            authorizationService,
            mossId
        )
    }

    @Bean
    open fun analysisScheduler(
        pullRequestRepository: PullRequestRepository,
        analysisService: AnalysisService,
        analysisResultRepository: AnalysisResultRepository,
        analysisStatusCheckService: AnalysisStatusCheckService
    ): AnalysisScheduler {
        return AnalysisScheduler(
            pullRequestRepository,
            analysisService,
            analysisResultRepository,
            analysisStatusCheckService
        )
    }

}