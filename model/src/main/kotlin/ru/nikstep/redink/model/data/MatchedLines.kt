package ru.nikstep.redink.model.data

/**
 * Data class for saving analysis line matches
 */
data class MatchedLines(
    val match1: Pair<Int, Int>,
    val match2: Pair<Int, Int>,
    val files: Pair<String, String>
)