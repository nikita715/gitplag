package io.gitplag.analysis.solutions

import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.data.PreparedAnalysisData
import io.gitplag.model.entity.Analysis
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.AnalyzerProperty
import java.io.File
import java.time.LocalDateTime

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
     * Get files of the [analysis] and the [user]
     */
    fun getAnalysisFiles(analysis: Analysis, user: String): List<File>

    /**
     * Delete files of the [analysis]
     */
    fun deleteAnalysisFiles(analysis: Analysis): Unit

    /**
     * Delete solution file of the repository
     */
    fun deleteSolutionFile(repo: Repository, branch: String, creator: String, fileName: String)

    /**
     * Delete solution file of the repository
     */
    fun deleteAllSolutionFiles(repo: Repository, branch: String, creator: String)

    /**
     * Delete base file of the repository
     */
    fun deleteBaseFile(repo: Repository, branch: String, fileName: String)

    /**
     * Delete base file of the repository
     */
    fun deleteAllBaseFiles(repo: Repository, branch: String)

    /**
     * Delete files of an analysis by its parameters
     */
    fun deleteAnalysisFiles(repoName: String, executionDate: LocalDateTime, analyzer: AnalyzerProperty)
}