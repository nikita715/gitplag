package ru.nikstep.redink.model.dto

import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.enums.AnalyserProperty
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language

/**
 * Dto for [Repository] class
 */
data class RepositoryDto(val gitService: GitProperty, val fullName: String) {
    var language: Language? = null
    var filePatterns: Collection<String>? = null
    var analyser: AnalyserProperty? = null
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
        analyser: AnalyserProperty?,
        periodicAnalysis: Boolean?,
        periodicAnalysisDelay: Int?,
        branches: List<String>?,
        analysisMode: AnalysisMode?,
        mossParameters: String?,
        jplagParameters: String?
    ) : this(gitService, fullName) {
        this.language = language
        this.filePatterns = filePatterns
        this.analyser = analyser
        this.periodicAnalysis = periodicAnalysis
        this.periodicAnalysisDelay = periodicAnalysisDelay
        this.branches = branches
        this.analysisMode = analysisMode
        this.mossParameters = mossParameters
        this.jplagParameters = jplagParameters
    }
}