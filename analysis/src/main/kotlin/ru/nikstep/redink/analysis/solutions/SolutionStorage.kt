package ru.nikstep.redink.analysis.solutions

import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.PreparedAnalysisData
import ru.nikstep.redink.model.data.SourceFileInfo
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.SolutionFileRecord
import ru.nikstep.redink.util.GitProperty
import java.io.File

/**
 * Storage of source files: teacher base files and student solutions
 */
interface SolutionStorage {

    /**
     * Load all base files from local storage
     */
    fun loadBases(settings: AnalysisSettings): List<File>

    /**
     * Load base file from local storage
     */
    fun loadBase(gitProperty: GitProperty, repoName: String, branchName: String, fileName: String): File

    /**
     * Save base files to local storage
     */
    fun saveBasesFromDir(tempDir: String, repo: Repository, branchName: String)

    /**
     * Save base file to local storage
     */
    fun saveBaseByText(repo: Repository, branch: String, fileName: String, fileText: String)

    /**
     * Save solution of [fileName] for [PullRequest.creatorName]
     */
    fun saveSolution(pullRequest: PullRequest, fileName: String, fileText: String): SolutionFileRecord

    /**
     * Save [sourceFileInfo] solution
     */
    fun saveSolution(sourceFileInfo: SourceFileInfo): SolutionFileRecord

    /**
     * Load all base solution files and corresponding solution files of the [pullRequest].
     * Merges all files of each students to single file.
     */
    fun loadBasesAndComposedSolutions(settings: AnalysisSettings, tempDir: String): PreparedAnalysisData

    /**
     * Load all base solution files and corresponding solution files of the [pullRequest]
     * and information about them. See [PreparedAnalysisData].
     */
    fun loadBasesAndSeparatedSolutions(settings: AnalysisSettings): PreparedAnalysisData

    /**
     * Load all base solution files and corresponding solution files of the [pullRequest].
     * Creates directories in [tempDir] for each student and copies each file of a student
     * to the student's directory, names them by digits (0.ext, 1.ext, etc.) and stores the corresponding names
     * in solution objects.
     */
    fun loadBasesAndSeparatedCopiedSolutions(settings: AnalysisSettings, tempDir: String): PreparedAnalysisData
}