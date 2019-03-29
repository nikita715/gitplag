package ru.nikstep.redink.analysis.solutions

import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.PreparedAnalysisData
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository

/**
 * Storage of source files: teacher base files and student solutions
 */
interface SourceCodeStorage {

    /**
     * Save all files from [tempDir] as base files that belong to the [repo]
     */
    fun saveBasesFromDir(tempDir: String, repo: Repository, branchName: String)

    /**
     * Save all files from [tempDir] as solution files that belong to the [pullRequest]
     */
    fun saveSolutionsFromDir(tempDir: String, pullRequest: PullRequest)

    /**
     * Load all base solution files and corresponding solution files according to the [settings].
     * Merges all files of each students to single file.
     */
    fun loadBasesAndComposedSolutions(settings: AnalysisSettings, tempDir: String): PreparedAnalysisData

    /**
     * Load all base solution files and corresponding solution files according to the [settings].
     */
    fun loadBasesAndSeparatedSolutions(settings: AnalysisSettings, tempDir: String): PreparedAnalysisData

    /**
     * Load all base solution files and corresponding solution files according to the [settings].
     * Creates directories in [tempDir] for each student and copies each file of a student
     * to the student's directory, names them by digits (0.ext, 1.ext, etc.) and stores the corresponding names
     * in solution objects.
     */
    fun loadBasesAndSeparatedCopiedSolutions(settings: AnalysisSettings, tempDir: String): PreparedAnalysisData

    fun prepareAnalysisData(settings: AnalysisSettings, tempDir: String): PreparedAnalysisData
}