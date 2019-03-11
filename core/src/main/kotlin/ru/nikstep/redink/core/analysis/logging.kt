package ru.nikstep.redink.core.analysis

import mu.KLogger
import ru.nikstep.redink.model.entity.Repository

internal inline fun <T> KLogger.loggedAnalysis(repository: Repository, action: () -> T): T {
    info {
        "Analysis: start analysing of repository  ${repository.name}"
    }
    val result = action()
    info {
        "Analysis: complete analysing of repository  ${repository.name}"
    }
    return result
}

internal fun KLogger.exceptionAtAnalysisOf(throwable: Throwable, repository: Repository) {
    error(throwable) { "Analysis: exception at the analysis of repo ${repository.name}\n" }
}