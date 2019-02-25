package ru.nikstep.redink.analysis.solutions

import ru.nikstep.redink.analysis.PreparedAnalysisFiles
import ru.nikstep.redink.model.entity.AnalysisPair
import ru.nikstep.redink.model.entity.PullRequest
import java.io.File

interface SolutionStorageService {
    fun loadBase(repoName: String, fileName: String): File
    fun saveBase(pullRequest: PullRequest, fileName: String, fileText: String): File
    fun loadSolution(repoName: String, userName: String, fileName: String): File
    fun loadSolution1(analysisPair: AnalysisPair): File
    fun loadSolution2(analysisPair: AnalysisPair): File
    fun saveSolution(pullRequest: PullRequest, fileName: String, fileText: String): File
    fun loadBaseAndSolutions(repoName: String, fileName: String): PreparedAnalysisFiles
    fun loadAllBasesAndSolutions(pullRequest: PullRequest): Collection<PreparedAnalysisFiles>
}