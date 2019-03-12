package ru.nikstep.redink.model.data

import ru.nikstep.redink.util.GitProperty

/**
 * Data class for saving analysis results
 */
data class AnalysisResult(
    val repo: String,
    val gitService: GitProperty,
    val resultLink: String,
    val matchData: List<AnalysisMatch>
) {
    constructor(analysisSettings: AnalysisSettings, resultLink: String, matchData: List<AnalysisMatch>) : this(
        analysisSettings.repository.name,
        analysisSettings.gitService,
        resultLink,
        matchData
    )
}

data class AnalysisMatch(
    val students: Pair<String, String>,
    val sha: Pair<String, String>,
    val lines: Int,
    val percentage: Int,
    val matchedLines: List<MatchedLines> = emptyList()
)