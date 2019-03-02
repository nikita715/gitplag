package ru.nikstep.redink.analysis

import io.kotlintest.matchers.shouldEqual
import org.junit.Test
import ru.nikstep.redink.analysis.analyser.JPlagAnalyser
import ru.nikstep.redink.model.data.AnalysisResult

class JPlagAnalyserTest {

    val jPlagAnalysisService =
        JPlagAnalyser(solutionStorageService, solutionsDir)

    val expectedResult = listOf(
        AnalysisResult(
            students = "student2" to "student1",
            countOfLines = -1,
            percentage = 100,
            repository = TEST_REPO_NAME,
            fileName = TEST_FILE_NAME,
            matchedLines = listOf((13 to 22) to (12 to 21))
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

    @Test
    fun analyse() {
        jPlagAnalysisService.analyse(pullRequest) shouldEqual expectedResult
    }
}