package ru.nikstep.redink.analysis

import ru.nikstep.redink.analysis.analyser.JPlagAnalyser
import ru.nikstep.redink.model.data.AnalysisResult

class JPlagAnalyserTest : AbstractAnalyserTest() {

    override val analysisService =
        JPlagAnalyser(solutionStorageService, solutionsDir)

    override val expectedResult = listOf(
        AnalysisResult(
            students = "student2" to "student1",
            countOfLines = -1,
            percentage = 100,
            repository = TEST_REPO_NAME,
            fileName = TEST_FILE_NAME,
            matchedLines = listOf((13 to 22) to (12 to 21))
        ),
        AnalysisResult(
            students = "student3" to "student2",
            countOfLines = -1,
            percentage = 55,
            repository = TEST_REPO_NAME,
            fileName = TEST_FILE_NAME,
            matchedLines = listOf((10 to 18) to (15 to 22))
        ),
        AnalysisResult(
            students = "student3" to "student1",
            countOfLines = -1,
            percentage = 55,
            repository = TEST_REPO_NAME,
            fileName = TEST_FILE_NAME,
            matchedLines = listOf((10 to 18) to (14 to 21))
        )
    )
}