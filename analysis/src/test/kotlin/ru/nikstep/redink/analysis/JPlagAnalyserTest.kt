package ru.nikstep.redink.analysis

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.matchers.shouldEqual
import org.junit.Test
import ru.nikstep.redink.analysis.analyser.JPlagAnalyser
import ru.nikstep.redink.model.data.AnalysisMatch
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.MatchedLines
import ru.nikstep.redink.util.RandomGenerator
import java.nio.file.Files

class JPlagAnalyserTest : AbstractAnalyserTest() {

    private val resultDir = Files.createTempDirectory("dir").toFile().absolutePath + "/"
    private val serverUrl = "url"

    private val prefix = "prefix"
    private val randomGenerator = mock<RandomGenerator> {
        on { randomAlphanumeric(10) } doReturn prefix
    }

    override val analysisService =
        JPlagAnalyser(solutionStorageService, randomGenerator, solutionsDir, resultDir, serverUrl)

    override val expectedResult =
        AnalysisResult(
            gitService = gitService,
            repo = testRepoName,
            resultLink = "$serverUrl/jplagresult/$prefix/index.html",
            matchData = listOf(
                AnalysisMatch(
                    students = "student2" to "student1",
//            sha = sha2 to sha1,
                    sha = "" to "",
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
//            sha = sha3 to sha2,
                    sha = "" to "",
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
//            sha = sha3 to sha1,
                    sha = "" to "",
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
        analysisResult shouldEqual expectedResult
    }
}