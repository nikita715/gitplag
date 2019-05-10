package io.gitplag.analysis.solutions

import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.data.PreparedAnalysisData
import io.gitplag.model.entity.Analysis
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import java.io.File

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
     * Merges all files of each student to single files.
     */
    fun loadBasesAndComposedSolutions(settings: AnalysisSettings, tempDir: String): PreparedAnalysisData

    /**
     * Load all base solution files and corresponding solution files according to the [settings].
     */
    fun loadBasesAndSeparatedSolutions(settings: AnalysisSettings, tempDir: String): PreparedAnalysisData

    /**
     * Get files of the [analysis] and the [user]
     */
    fun getAnalysisFiles(analysis: Analysis, user: String): List<File>

    /**
     * Delete files of the [analysis]
     */
    fun deleteAnalysisFiles(analysis: Analysis): Unit
}