package io.gitplag.analysis

import io.gitplag.model.data.AnalysisMatch
import io.gitplag.model.data.AnalysisResult
import io.gitplag.model.data.MatchedLines
import kotlin.math.roundToInt

/**
 * Merge a list of [results] to one [AnalysisResult]
 */
fun mergeAnalysisResults(results: List<AnalysisResult>) =
    AnalysisResult(
        repo = results.first().repo,
        resultLink = results.joinToString(separator = ";") { it.resultLink },
        executionDate = results.first().executionDate,
        matchData = mergeAnalysisMatches(results.flatMap { it.matchData })
    )

/**
 * Merge a list of [analysisMatches] to one [AnalysisMatch]
 */
fun mergeAnalysisMatches(analysisMatches: List<AnalysisMatch>) =
    analysisMatches.groupBy { it.students }.map { matchesEntry ->
        val matches = matchesEntry.value
        val percents = matches.map { it.percentage }
        AnalysisMatch(
            students = matchesEntry.key,
            percentage = percents.average().roundToInt(),
            minPercentage = percents.min() ?: 0,
            maxPercentage = percents.max() ?: 0,
            sha = matches.first().sha,
            createdAt = matches.first().createdAt,
            matchedLines = mergeMatchedLines(matches)
        )
    }

/**
 * Merge a list of [matches] to one list of [MatchedLines]
 */
fun mergeMatchedLines(matches: List<AnalysisMatch>): List<MatchedLines> {
    return matches.first().matchedLines
}