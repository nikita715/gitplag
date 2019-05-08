package io.gitplag.git

import io.gitplag.model.data.AnalysisSettings
import mu.KLogger

internal inline fun <T> KLogger.loggedAnalysis(settings: AnalysisSettings, action: () -> T): T {
    info {
        "Analysis: start analysing of repository  ${settings.repository.name}"
    }
    val result = action()
    info {
        "Analysis: complete analysing of repository  ${settings.repository.name}"
    }
    return result
}