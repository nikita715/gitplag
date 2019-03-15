package ru.nikstep.redink.analysis

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.kotlintest.matchers.shouldEqual
import org.junit.Test
import ru.nikstep.redink.analysis.analyser.JPlagAnalyser
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.*
import ru.nikstep.redink.model.entity.JPlagReport
import ru.nikstep.redink.model.repo.JPlagReportRepository
import ru.nikstep.redink.util.Language
import ru.nikstep.redink.util.RandomGenerator
import java.nio.file.Files
import java.time.LocalDateTime

class JPlagAnalyserTest : AbstractAnalyserTest() {

    private val resultDir = Files.createTempDirectory("dir").toFile().absolutePath + "/"
    private val serverUrl = "url"

    private val prefix = "prefix"
    private val randomGenerator = mock<RandomGenerator> {
        on { randomAlphanumeric(10) } doReturn prefix
    }

    private val testPreparedAnalysisFiles = PreparedAnalysisData(
        testRepoName,
        Language.JAVA,
        listOf(base),
        listOf(
            Solution(student1, testFileName, solution1, sha = sha1),
            Solution(student2, testFileName, solution2, sha = sha2),
            Solution(student3, testFileName, solution3, sha = sha3)
        )
    )

    private val solutionStorageService = mock<SolutionStorage> {
        on { loadBasesAndSeparateSolutions(any()) } doReturn testPreparedAnalysisFiles
    }

    private val jPlagReportRepository = mock<JPlagReportRepository>()

    override val analysisService =
        JPlagAnalyser(
            solutionStorageService,
            randomGenerator,
            jPlagReportRepository,
            solutionsDir,
            resultDir,
            serverUrl
        )

    override val expectedResult =
        AnalysisResult(
            gitService = gitService,
            repo = testRepoName,
            resultLink = "$serverUrl/jplagresult/$prefix/index.html",
            executionDate = LocalDateTime.now(),
            matchData = listOf(
                AnalysisMatch(
                    students = "student2" to "student1",
                    sha = sha2 to sha1,
                    lines = -1,
                    percentage = 100,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 13 to 22,
                            match2 = 12 to 21,
                            files = testFileName to testFileName
                        )
                    )
                ), AnalysisMatch(
                    students = "student3" to "student2",
                    sha = sha3 to sha2,
                    lines = -1,
                    percentage = 55,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 10 to 18,
                            match2 = 15 to 22,
                            files = testFileName to testFileName
                        )
                    )
                ), AnalysisMatch(
                    sha = sha3 to sha1,
                    students = "student3" to "student1",
                    lines = -1,
                    percentage = 55,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 10 to 18,
                            match2 = 14 to 21,
                            files = testFileName to testFileName
                        )
                    )
                )

            )
        )

    @Test
    fun analyse() {
        val analysisResult = analysisService.analyse(analysisSettings)
        analysisResult shouldEqual expectedResult.copy(executionDate = analysisResult.executionDate)
        verify(jPlagReportRepository).save(
            JPlagReport(
                createdAt = analysisResult.executionDate,
                hash = randomGenerator.randomAlphanumeric(10)
            )
        )
    }
}