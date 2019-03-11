package ru.nikstep.redink.core.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.core.graphql.AnalysisPairQuery
import ru.nikstep.redink.core.graphql.LocalDateTimeScalarType
import ru.nikstep.redink.model.repo.AnalysisRepository
import ru.nikstep.redink.model.repo.RepositoryRepository

/**
 * Configuration of graphql services
 */
@Configuration
class GraphqlConfig {

    /**
     * [AnalysisPairQuery] bean
     */
    @Bean
    fun analysisPairQuery(repositoryRepository: RepositoryRepository, analysisRepository: AnalysisRepository) =
        AnalysisPairQuery(repositoryRepository, analysisRepository)


    /**
     * [LocalDateTimeScalarType] bean
     */
    @Bean
    fun localDateTimeScalarType() = LocalDateTimeScalarType()
}