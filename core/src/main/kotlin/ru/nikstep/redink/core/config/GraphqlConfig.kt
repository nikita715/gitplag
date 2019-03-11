package ru.nikstep.redink.core.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.analysis.AnalysisRunner
import ru.nikstep.redink.core.graphql.AnalysisQueries
import ru.nikstep.redink.core.graphql.LocalDateTimeScalarType
import ru.nikstep.redink.model.repo.AnalysisRepository
import ru.nikstep.redink.model.repo.RepositoryRepository

/**
 * Configuration of graphql services
 */
@Configuration
class GraphqlConfig {

    /**
     * [AnalysisQueries] bean
     */
    @Bean
    fun analysisPairQuery(
        repositoryRepository: RepositoryRepository,
        analysisRepository: AnalysisRepository,
        analysisRunner: AnalysisRunner
    ) =
        AnalysisQueries(repositoryRepository, analysisRepository, analysisRunner)


    /**
     * [LocalDateTimeScalarType] bean
     */
    @Bean
    fun localDateTimeScalarType() = LocalDateTimeScalarType()
}