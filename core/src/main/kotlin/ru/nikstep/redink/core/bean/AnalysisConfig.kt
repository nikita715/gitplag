package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import ru.nikstep.redink.analysis.FileSystemSolutionLoadingService
import ru.nikstep.redink.analysis.MossAnalysisService
import ru.nikstep.redink.analysis.SolutionLoadingService
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository

@Configuration
open class AnalysisConfig {

    @Bean
    open fun solutionLoadingService(
        sourceCodeRepository: SourceCodeRepository,
        repositoryRepository: RepositoryRepository
    ): SolutionLoadingService {
        return FileSystemSolutionLoadingService(sourceCodeRepository, repositoryRepository)
    }

    @Bean
    open fun analysisService(
        solutionLoadingService: SolutionLoadingService,
        repositoryRepository: RepositoryRepository,
        env: Environment
    ): MossAnalysisService {
        val mossId = env.getProperty("MOSS_ID")
        return MossAnalysisService(solutionLoadingService, repositoryRepository, mossId)
    }

}