package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.repo.AnalysisPairRepository
import ru.nikstep.redink.results.AnalysisResultViewBuilder

@Configuration
class ResultsConfig {

    @Bean
    fun analysisResultViewBuilder(
        analysisPairRepository: AnalysisPairRepository,
        solutionStorage: SolutionStorage
    ): AnalysisResultViewBuilder {
        return AnalysisResultViewBuilder(analysisPairRepository, solutionStorage)
    }

}