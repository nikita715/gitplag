package ru.nikstep.redink.analysis

import java.io.File

interface SolutionLoadingService {
    fun loadSolutions(repoName: String, fileName: String): Pair<File, List<File>>
}