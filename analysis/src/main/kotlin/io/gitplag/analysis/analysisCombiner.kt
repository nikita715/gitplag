package io.gitplag.analysis

import io.gitplag.model.data.AnalysisMatch
import io.gitplag.model.data.AnalysisResult
import io.gitplag.model.data.MatchedLines
import kotlin.math.roundToInt

fun mergeAnalysisResults(vararg results: AnalysisResult) =
    AnalysisResult(
        repo = results.first().repo,
        resultLink = results.joinToString(separator = ";") { it.resultLink },
        executionDate = results.first().executionDate,
        matchData = mergeAnalysisMatches(results.flatMap { it.matchData })
    )

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


fun mergeMatchedLines(matches: List<AnalysisMatch>): List<MatchedLines> {
    return matches.first().matchedLines
}