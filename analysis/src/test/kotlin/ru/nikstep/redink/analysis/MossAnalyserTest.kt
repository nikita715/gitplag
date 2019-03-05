package ru.nikstep.redink.analysis

import ru.nikstep.redink.analysis.analyser.MossAnalyser
import ru.nikstep.redink.model.data.AnalysisResult

class MossAnalyserTest : AbstractAnalyserTest() {

    override val analysisService =
        MossAnalyser(solutionStorageService, System.getenv("MOSS_ID"))

    override val expectedResult = listOf(
        AnalysisResult(
            students = "student1" to "student2",
            countOfLines = 14,
            percentage = 66,
            repository = TEST_REPO_NAME,
            fileName = TEST_FILE_NAME,
            matchedLines = listOf((7 to 19) to (7 to 20))
        ),
        AnalysisResult(
            students = "student2" to "student3",
            countOfLines = 5,
            percentage = 22,
            repository = TEST_REPO_NAME,
            fileName = TEST_FILE_NAME,
            matchedLines = listOf((16 to 20) to (12 to 16))
        ),
        AnalysisResult(
            students = "student1" to "student3",
            countOfLines = 5,
            percentage = 22,
            repository = TEST_REPO_NAME,
            fileName = TEST_FILE_NAME,
            matchedLines = listOf((15 to 19) to (12 to 16))
        )
    )
}