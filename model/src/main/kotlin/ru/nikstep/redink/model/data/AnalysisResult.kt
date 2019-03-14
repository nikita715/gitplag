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
        analysisSettings.gitService,
        resultLink,
        executionDate,
        matchData
    )
}

data class AnalysisMatch(
    val students: Pair<String, String>,
    val lines: Int,
    val percentage: Int,
    val matchedLines: List<MatchedLines> = emptyList(),
    val sha: Pair<String, String>
)