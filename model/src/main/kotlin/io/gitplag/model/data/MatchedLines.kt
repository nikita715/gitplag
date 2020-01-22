package io.gitplag.model.data

import io.gitplag.model.enums.AnalyzerProperty

/**
 * Data class for saving analysis line matches
 */
data class MatchedLines(
    val match1: Pair<Int, Int>,
    val match2: Pair<Int, Int>,
    val files: Pair<String, String>,
    val analyzer: AnalyzerProperty
)