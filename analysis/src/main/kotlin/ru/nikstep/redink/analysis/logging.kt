package ru.nikstep.redink.analysis

import mu.KLogger
import ru.nikstep.redink.model.entity.Repository

internal inline fun KLogger.loggedAnalysis(repository: Repository, action: () -> Unit) {
    info {
        "Analysis: start analysing of repository  ${repository.name}"
    }
    action()
    info {
        "Analysis: complete analysing of repository  ${repository.name}"
    }
}

internal fun KLogger.exceptionAtAnalysisOf(throwable: Throwable, repository: Repository) {
    error(throwable) { "Analysis: exception at the analysis of repo ${repository.name}\n" }
}