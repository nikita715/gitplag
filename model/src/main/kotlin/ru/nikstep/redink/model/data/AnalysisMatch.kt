package ru.nikstep.redink.model.data

data class AnalysisMatch(
    val students: Pair<String, String>,
    val lines: Int,
    val percentage: Int,
    val matchedLines: List<MatchedLines> = emptyList(),
    val sha: Pair<String, String>
)