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
    val branch: String
) {
    var analyser: AnalyserProperty = repository.analyser
        private set(value) {
            field = value
        }
    var language: Language = repository.language
        private set(value) {
            field = value
        }
    var mode: AnalysisMode = repository.analysisMode
        private set(value) {
            field = value
        }

    constructor(
        repository: Repository, branch: String, analyser: AnalyserProperty? = repository.analyser
        , language: Language? = repository.language
        , mode: AnalysisMode? = repository.analysisMode
    ) : this(repository, branch) {
        this.analyser = analyser ?: repository.analyser
        this.language = language ?: repository.language
        this.mode = mode ?: repository.analysisMode
    }
}