package io.gitplag.model.dto

import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language

/**
 * Dto for [Repository] class
 */
data class RepositoryDto(val id: Long, val git: GitProperty, val name: String) {
    var language: Language? = null
    var filePatterns: Collection<String>? = null
    var analyzer: AnalyzerProperty? = null
    var analysisMode: AnalysisMode? = null
    var mossParameters: String? = null
    var jplagParameters: String? = null
}