package io.gitplag.model.dto

import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.Language

/**
 * Analysis request dto
 */
class AnalysisDto(
    val repoId: Long,
    val branch: String,
    val analyzer: AnalyzerProperty?,
    val language: Language?,
    val mode: AnalysisMode?,
    val parameters: String?,
    val responseUrl: String?
)