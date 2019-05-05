package io.gitplag.core.beans

import io.gitplag.model.manager.AnalysisResultDataManager
import io.gitplag.model.manager.JPlagReportDataManager
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.util.RandomGenerator
import org.springframework.context.support.beans
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

val coreBeans = beans {
    // Data managers
    bean<RepositoryDataManager>()
    bean<AnalysisResultDataManager>()

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