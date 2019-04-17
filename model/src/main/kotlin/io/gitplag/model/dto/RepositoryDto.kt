package io.gitplag.model.dto

import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language

/**
 * Dto for [Repository] class
 */
data class RepositoryDto(val gitService: GitProperty, val fullName: String) {
    var language: Language? = null
    var filePatterns: Collection<String>? = null
    var analyzer: AnalyzerProperty? = null
    var periodicAnalysis: Boolean? = null
    var periodicAnalysisDelay: Int? = null
    var branches: List<String>? = null
    var analysisMode: AnalysisMode? = null
    var mossParameters: String? = null
    var jplagParameters: String? = null

    constructor(
        gitService: GitProperty, fullName: String,
        language: Language?,
        filePatterns: Collection<String>?,
        analyzer: AnalyzerProperty?,
        periodicAnalysis: Boolean?,
        periodicAnalysisDelay: Int?,
        branches: List<String>?,
        analysisMode: AnalysisMode?,
        mossParameters: String?,
        jplagParameters: String?
    ) : this(gitService, fullName) {
        this.language = language
        this.filePatterns = filePatterns
        this.analyzer = analyzer
        this.periodicAnalysis = periodicAnalysis
        this.periodicAnalysisDelay = periodicAnalysisDelay
        this.branches = branches
        this.analysisMode = analysisMode
        this.mossParameters = mossParameters
        this.jplagParameters = jplagParameters
    }
}