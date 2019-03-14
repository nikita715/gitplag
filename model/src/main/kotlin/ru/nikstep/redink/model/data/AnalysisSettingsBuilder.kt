package ru.nikstep.redink.model.data

import mu.KotlinLogging
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.AnalysisBranchMode
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
        branch = branch,
        branchMode = branchMode,
        withLines = withLines,
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
        branch = branch,
        branchMode = branchMode,
        withLines = withLines,
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

/**
 * Set the [branches] to the [AnalysisSettings]
 */
fun AnalysisSettings.branch(branch: String?): AnalysisSettings {
    if (branch != null) {
        try {
            AnalysisSettings(
                repository = repository,
                analyser = analyser,
                gitService = gitService,
                branch = branch,
                branchMode = branchMode,
                withLines = withLines,
                language = language
            )
        } catch (e: IllegalArgumentException) {
            logger.error { "Analysis: wrong branches \"$language\"" }
        }
    }
    return this
}

/**
 * Set the [language] to the [AnalysisSettings]
 */
fun AnalysisSettings.branchMode(branchMode: String?): AnalysisSettings {
    if (branchMode != null) {
        try {
            return branchMode(enumValueOf<AnalysisBranchMode>(branchMode.toUpperCase()))
        } catch (e: IllegalArgumentException) {
            logger.error { "Analysis: wrong branch mode name \"$branchMode\"" }
        }
    }
    return this
}

/**
 * Set the [language] to the [AnalysisSettings]
 */
fun AnalysisSettings.branchMode(branchMode: AnalysisBranchMode): AnalysisSettings =
    AnalysisSettings(
        repository = repository,
        analyser = analyser,
        gitService = gitService,
        branch = branch,
        branchMode = branchMode,
        withLines = withLines,
        language = language
    )

/**
 * Set the [withLines] to the [AnalysisSettings]
 */
fun AnalysisSettings.withLines(withLines: Boolean): AnalysisSettings =
    AnalysisSettings(
        repository = repository,
        analyser = analyser,
        gitService = gitService,
        branch = branch,
        branchMode = branchMode,
        withLines = withLines,
        language = language
    )