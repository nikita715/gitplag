package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.core.graphql.AnalysisPairQuery
import ru.nikstep.redink.model.repo.AnalysisPairRepository

@Configuration
class GraphqlConfig {

    @Bean
    fun analysisPairQuery(analysisPairRepository: AnalysisPairRepository) =
        AnalysisPairQuery(analysisPairRepository)

}