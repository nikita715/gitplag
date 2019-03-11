package ru.nikstep.redink.analysis.solutions

import ru.nikstep.redink.analysis.data.AnalysisSettings
import ru.nikstep.redink.analysis.data.PreparedAnalysisFiles
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.GitProperty
import java.io.File

/**
 * Storage of source files: teacher base files and student solutions
 */
interface SolutionStorage {

    /**
     * Load all base files from local storage
     */
    fun loadBases(repository: Repository): List<File>

    /**
     * Load base file from local storage
     */
    fun loadBase(repository: Repository, fileName: String): File

    /**
     * Load base file from local storage
     */
    fun loadBase(gitProperty: GitProperty, repoName: String, fileName: String): File

    /**
     * Save base file to local storage
     */
    fun saveBase(pullRequest: PullRequest, fileName: String, fileText: String): File

    /**
     * Load solution file of the student from local storage
     */
    fun loadSolution(repository: Repository, userName: String, fileName: String): File

    /**
     * Save solution of [fileName] for [PullRequest.creatorName]
     */
    fun saveSolution(pullRequest: PullRequest, fileName: String, fileText: String): File

    /**
     * Load all base solution files and corresponding solution files of the [pullRequest]
     * @return all required files for each fileName
     * and information about them. See [PreparedAnalysisFiles]
     */
    fun loadAllBasesAndSolutions(analysisSettings: AnalysisSettings): PreparedAnalysisFiles

    /**
     * Get count of files that belong to the [repository]
     */
    fun getCountOfSolutionFiles(repository: Repository, fileName: String): Int
}