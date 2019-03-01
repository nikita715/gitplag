package ru.nikstep.redink.model.data

data class AnalysisResult(
    val students: Pair<String, String>,
    val countOfLines: Int,
    val percentage: Int,
    val repository: String,
    val fileName: String,
    val matchedLines: List<Pair<Pair<Int, Int>, Pair<Int, Int>>> = emptyList()
)