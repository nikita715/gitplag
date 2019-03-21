package ru.nikstep.redink.model.data

import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.enums.AnalyserProperty
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.model.enums.Language

/**
 * Input analysis properties
 */
class AnalysisSettings(
    val repository: Repository,
    val branch: String,
    val analyser: AnalyserProperty = repository.analyser,
    val language: Language = repository.language,
    val mode: AnalysisMode = repository.analysisMode
)