package ru.nikstep.redink.analysis.solutions

import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.PreparedAnalysisData
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty
import java.io.File

/**
 * Storage of source files: teacher base files and student solutions
 */
interface SolutionStorage {

    /**
     * Load all base files from local storage
     */
    fun loadBases(analysisSettings: AnalysisSettings): List<File>

    /**
     * Load base file from local storage
     */
    fun loadBase(gitProperty: GitProperty, repoName: String, branchName: String, fileName: String): File

    /**
     * Save base file to local storage
     */
    fun saveBase(pullRequest: PullRequest, fileName: String, fileText: String): File

    /**
     * Save solution of [fileName] for [PullRequest.creatorName]
     */
    fun saveSolution(pullRequest: PullRequest, fileName: String, fileText: String): File

    /**
     * Load all base solution files and corresponding solution files of the [pullRequest]
     * @return all required files for each fileName
     * and information about them. See [PreparedAnalysisData]
     */
    fun loadAllBasesAndSolutions(analysisSettings: AnalysisSettings): PreparedAnalysisData
}