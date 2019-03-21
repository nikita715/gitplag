package ru.nikstep.redink.model.dto

import ru.nikstep.redink.model.enums.AnalyserProperty
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language

class RepositoryDto(val gitService: GitProperty, val fullName: String) {
    val language: Language? = null
    val filePatterns: Collection<String>? = null
    val analyser: AnalyserProperty? = null
    val periodicAnalysis: Boolean? = null
    val periodicAnalysisDelay: Int? = null
    val branches: List<String>? = null
    val analysisMode: AnalysisMode? = null
}