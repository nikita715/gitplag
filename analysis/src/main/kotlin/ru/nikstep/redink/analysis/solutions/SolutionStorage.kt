package ru.nikstep.redink.analysis.solutions

import ru.nikstep.redink.analysis.PreparedAnalysisFiles
import ru.nikstep.redink.model.entity.AnalysisPair
import ru.nikstep.redink.model.entity.PullRequest
import java.io.File

/**
 * Storage of source files: teacher base files and student solutions
 */
interface SolutionStorage {

    /**
     * Load base file from local storage
     */
    fun loadBase(repoName: String, fileName: String): File

    /**
     * Save base file to local storage
     */
    fun saveBase(pullRequest: PullRequest, fileName: String, fileText: String): File

    /**
     * Load solution file of the student from local storage
     */
    fun loadSolution(repoName: String, userName: String, fileName: String): File

    /**
     * Load solution file of the [AnalysisPair.student1] from local storage
     * @param analysisPair result of the analysis
     */
    fun loadSolution1(analysisPair: AnalysisPair): File

    /**
     * Load solution file of the [AnalysisPair.student2] from local storage
     * @param analysisPair result of the analysis
     */
    fun loadSolution2(analysisPair: AnalysisPair): File

    /**
     * Save solution of [fileName] for [PullRequest.creatorName]
     */
    fun saveSolution(pullRequest: PullRequest, fileName: String, fileText: String): File

    /**
     * Load all base solution files and corresponding solution files of the [pullRequest]
     * @return all required files for each fileName
     * and information about them. See [PreparedAnalysisFiles]
     */
    fun loadAllBasesAndSolutions(pullRequest: PullRequest): Collection<PreparedAnalysisFiles>

    fun getCountOfSolutionFiles(repoName: String, fileName: String): Int
}