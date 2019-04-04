package ru.nikstep.redink.core.beans

import org.springframework.context.support.beans
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import ru.nikstep.redink.core.graphql.AnalysisQueries
import ru.nikstep.redink.core.graphql.LocalDateTimeScalarType
import ru.nikstep.redink.core.graphql.RepositoryQueries
import ru.nikstep.redink.core.graphql.SourceFileQueries
import ru.nikstep.redink.model.manager.AnalysisResultDataManager
import ru.nikstep.redink.model.manager.JPlagReportDataManager
import ru.nikstep.redink.model.manager.RepositoryDataManager
import ru.nikstep.redink.util.RandomGenerator

val coreBeans = beans {
    // Data managers
    bean<RepositoryDataManager>()
    bean<AnalysisResultDataManager>()

    // Graphql
    bean<AnalysisQueries>()
    bean<RepositoryQueries>()
    bean<SourceFileQueries>()
    bean<LocalDateTimeScalarType>()

    // Main analysis TaskExecutor
    bean<TaskExecutor>("analysisTaskExecutor") {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 4
        executor.maxPoolSize = 4
        executor.initialize()
        executor
    }

    bean<RandomGenerator>()
    bean<JPlagReportDataManager>()
}