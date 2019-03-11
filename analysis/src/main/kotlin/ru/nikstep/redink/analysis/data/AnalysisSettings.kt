package ru.nikstep.redink.analysis.data

import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language

/**
 * Input analysis properties
 */
class AnalysisSettings(
    val repository: Repository,
    val analyser: AnalyserProperty = repository.analyser,
    val gitService: GitProperty = repository.gitService,
    val language: Language = repository.language
)