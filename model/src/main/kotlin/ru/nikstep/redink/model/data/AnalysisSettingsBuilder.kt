package ru.nikstep.redink.model.data

import mu.KotlinLogging
import ru.nikstep.redink.model.enums.AnalyserProperty
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.model.enums.Language

private val logger = KotlinLogging.logger {}

/**
 * Set the [analyser] to the [AnalysisSettings]
 */
fun AnalysisSettings.analyser(analyser: AnalyserProperty): AnalysisSettings =
    AnalysisSettings(
        repository = repository,
        analyser = analyser,
        branch = branch,
        language = language,
        mode = mode
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
        branch = branch,
        language = language,
        mode = mode
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

/**
 * Set the [branch] to the [AnalysisSettings]
 */
fun AnalysisSettings.branch(branch: String?): AnalysisSettings {
    if (branch != null) {
        try {
            AnalysisSettings(
                repository = repository,
                analyser = analyser,
                branch = branch,
                language = language,
                mode = mode
            )
        } catch (e: IllegalArgumentException) {
            logger.error { "Analysis: wrong branches \"$language\"" }
        }
    }
    return this
}

/**
 * Set the [mode] to the [AnalysisSettings]
 */
fun AnalysisSettings.mode(mode: AnalysisMode): AnalysisSettings =
    AnalysisSettings(
        repository = repository,
        analyser = analyser,
        branch = branch,
        language = language,
        mode = mode
    )

/**
 * Set the [mode] to the [AnalysisSettings]
 */
fun AnalysisSettings.mode(mode: String?): AnalysisSettings {
    if (mode != null) {
        try {
            return this.mode(enumValueOf<AnalysisMode>(mode.toUpperCase()))
        } catch (e: IllegalArgumentException) {
            logger.error { "Analysis: wrong language name \"$language\"" }
        }
    }
    return this
}