package ru.nikstep.redink.model.data

import mu.KotlinLogging
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.Language

private val logger = KotlinLogging.logger {}

/**
 * Set the [analyser] to the [AnalysisSettings]
 */
fun AnalysisSettings.analyser(analyser: AnalyserProperty): AnalysisSettings =
    AnalysisSettings(
        repository = repository,
        analyser = analyser,
        gitService = gitService,
        language = language
    )

/**
 * Set the [analyser] to the [AnalysisSettings]
 */
fun AnalysisSettings.analyser(analyser: String?): AnalysisSettings {
    if (analyser != null) {
        try {
            return this.analyser(enumValueOf<AnalyserProperty>(analyser.toUpperCase()))
        } catch (e: IllegalArgumentException) {
            logger.error { "Analysis: wrong analyser name \"$analyser\"" }
        }
    }
    return this
}

/**
 * Set the [language] to the [AnalysisSettings]
 */
fun AnalysisSettings.language(language: Language): AnalysisSettings =
    AnalysisSettings(
        repository = repository,
        analyser = analyser,
        gitService = gitService,
        language = language
    )

/**
 * Set the [language] to the [AnalysisSettings]
 */
fun AnalysisSettings.language(language: String?): AnalysisSettings {
    if (language != null) {
        try {
            return this.language(enumValueOf<Language>(language.toUpperCase()))
        } catch (e: IllegalArgumentException) {
            logger.error { "Analysis: wrong language name \"$language\"" }
        }
    }
    return this
}