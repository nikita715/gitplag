package io.gitplag.model.data

import java.time.LocalDateTime

/**
 * Data class for saving analysis match of two students
 */
data class AnalysisMatch(
    val students: Pair<String, String>,
    val lines: Int,
    val percentage: Int,
    val minPercentage: Int,
    val maxPercentage: Int,
    val matchedLines: List<MatchedLines> = emptyList(),
    val sha: Pair<String, String>,
    val createdAt: Pair<LocalDateTime, LocalDateTime>
)