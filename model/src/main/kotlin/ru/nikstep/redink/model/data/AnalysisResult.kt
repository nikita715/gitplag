package ru.nikstep.redink.model.data

import ru.nikstep.redink.util.GitProperty

/**
 * Data class for storing analysis pairs data
 */
data class AnalysisResult(
    val students: Pair<String, String>,
    val sha: Pair<String, String>,
    val gitService: GitProperty,
    val countOfLines: Int,
    val percentage: Int,
    val repository: String,
    val fileName: String,
    val matchedLines: List<Pair<Pair<Int, Int>, Pair<Int, Int>>> = emptyList()
)