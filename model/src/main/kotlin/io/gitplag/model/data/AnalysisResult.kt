package io.gitplag.model.data

import java.time.LocalDateTime

/**
 * Data class for saving analysis results
 */
data class AnalysisResult(
    val repo: String,
    val resultLink: String,
    val executionDate: LocalDateTime,
    val matchData: List<AnalysisMatch>,
    val hash: String
) {
    constructor(
        analysisSettings: AnalysisSettings,
        resultLink: String,
        executionDate: LocalDateTime,
        matchData: List<AnalysisMatch>,
        hash: String
    ) : this(
        analysisSettings.repository.name,
        resultLink,
        executionDate,
        matchData,
        hash
    )
}
