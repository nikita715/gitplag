package ru.nikstep.redink.model.data

import ru.nikstep.redink.util.GitProperty

data class AnalysisResult(
    val students: Pair<String, String>,
    val sha: Pair<String, String>,
    val lines: Int,
    val percentage: Int,
    val repo: String,
    val gitService: GitProperty,
    val matchedLines: List<MatchedLines> = emptyList()
)