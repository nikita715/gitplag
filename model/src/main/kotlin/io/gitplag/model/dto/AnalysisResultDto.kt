package io.gitplag.model.dto

import io.gitplag.model.entity.Analysis
import io.gitplag.model.enums.AnalyzerProperty
import java.time.LocalDateTime

/**
 * Analysis result dto without line matches
 */
class AnalysisResultDto(
    val id: Long,
    val repo: Long,
    val repoName: String,
    val analyzer: AnalyzerProperty,
    val branch: String,
    val date: LocalDateTime,
    val resultLink: String,
    val analysisPairs: List<AnalysisResultSimplePairDto>
) {
    constructor(analysis: Analysis, nameMap: Map<String, String> = emptyMap()) : this(
        analysis.id, analysis.repository.id, analysis.repository.name, analysis.analyzer,
        analysis.branch, analysis.executionDate, analysis.resultLink,
        analysis.analysisPairs.map { AnalysisResultSimplePairDto(it, nameMap) }.sortedByDescending { it.percentage }
    )
}