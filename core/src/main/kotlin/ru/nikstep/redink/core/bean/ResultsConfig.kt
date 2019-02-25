package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.analysis.solutions.SolutionStorageService
import ru.nikstep.redink.model.repo.AnalysisPairRepository
import ru.nikstep.redink.results.AnalysisResultViewBuilder

@Configuration
class ResultsConfig {

    @Bean
    fun analysisResultViewBuilder(
        analysisPairRepository: AnalysisPairRepository,
        solutionStorageService: SolutionStorageService
    ): AnalysisResultViewBuilder {
        return AnalysisResultViewBuilder(analysisPairRepository, solutionStorageService)
    }

}