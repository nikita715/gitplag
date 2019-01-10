package ru.nikstep.redink.entity

data class AnalysisMatch(
    val students: Pair<String, String>,
    val lines: Int,
    val link: String,
    val percentage: Int
)