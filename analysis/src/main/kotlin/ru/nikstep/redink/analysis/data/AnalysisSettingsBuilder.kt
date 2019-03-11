package ru.nikstep.redink.analysis.data

import mu.KotlinLogging
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.Language

private val logger = KotlinLogging.logger {}

fun AnalysisSettings.analyser(analyser: AnalyserProperty): AnalysisSettings =
    AnalysisSettings(
        repository = repository,
        analyser = analyser,
        gitService = gitService,
        language = language
    )

fun AnalysisSettings.analyser(analyser: String?): AnalysisSettings {
    if (analyser != null) {
        try {
            return this.analyser(enumValueOf<AnalyserProperty>(analyser.toUpperCase()))
        } catch (e: Exception) {
            logger.error { "Analysis: wrong analyser name \"$analyser\"" }
        }
    }
    return this
}

fun AnalysisSettings.language(language: Language): AnalysisSettings =
    AnalysisSettings(
        repository = repository,
        analyser = analyser,
        gitService = gitService,
        language = language
    )

fun AnalysisSettings.language(language: String?): AnalysisSettings {
    if (language != null) {
        try {
            return this.language(enumValueOf<Language>(language.toUpperCase()))
        } catch (e: Exception) {
            logger.error { "Analysis: wrong language name \"$language\"" }
        }
    }
    return this
}