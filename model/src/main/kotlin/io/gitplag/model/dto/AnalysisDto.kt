package io.gitplag.model.dto

import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.Language

/**
 * Analysis request dto
 */
class AnalysisDto(
    val branch: String,
    val analyzer: AnalyzerProperty?,
    val language: Language?,
    val mode: AnalysisMode?,
    val maxResultSize: Int?,
    val minResultPercentage: Int?,
    val responseUrl: String?,
    val updateFiles: Boolean = false,
    val additionalRepositories: List<Long>?
)