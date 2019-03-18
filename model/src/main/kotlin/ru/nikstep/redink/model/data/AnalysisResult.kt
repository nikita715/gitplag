package ru.nikstep.redink.model.data

import ru.nikstep.redink.util.GitProperty
import java.time.LocalDateTime

/**
 * Data class for saving analysis results
 */
data class AnalysisResult(
    val repo: String,
    val gitService: GitProperty,
    val resultLink: String,
    val executionDate: LocalDateTime,
    val matchData: List<AnalysisMatch>
) {
    constructor(
        analysisSettings: AnalysisSettings,
        resultLink: String,
        executionDate: LocalDateTime,
        matchData: List<AnalysisMatch>
    ) : this(
        analysisSettings.repository.name,
        analysisSettings.repository.gitService,
        resultLink,
        executionDate,
        matchData
    )
}
