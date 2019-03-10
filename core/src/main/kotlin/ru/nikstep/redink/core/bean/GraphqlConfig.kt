package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.core.graphql.AnalysisPairQuery
import ru.nikstep.redink.model.repo.AnalysisRepository
import ru.nikstep.redink.model.repo.RepositoryRepository

@Configuration
class GraphqlConfig {

    @Bean
    fun analysisPairQuery(repositoryRepository: RepositoryRepository, analysisRepository: AnalysisRepository) =
        AnalysisPairQuery(repositoryRepository, analysisRepository)

}