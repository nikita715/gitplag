package ru.nikstep.redink.analysis

import com.nhaarman.mockitokotlin2.any
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.mock.`when`
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito
import ru.nikstep.redink.analysis.analyser.MossAnalyser
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.AnalysisMatch
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.MatchedLines
import ru.nikstep.redink.model.data.PreparedAnalysisData
import ru.nikstep.redink.model.data.Solution
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language
import ru.nikstep.redink.util.asPath
import java.io.File
import java.time.LocalDateTime

class MossAnalyserTest : AbstractAnalyserTest() {

    private val separateSolutionsDir = asPath("src", "test", "resources", "mossPreparedFiles")

    private val solutionStorage = Mockito.mock(SolutionStorage::class.java)

    private val file1 = File("$separateSolutionsDir/$student1.txt")
    private val file2 = File("$separateSolutionsDir/$student2.txt")
    private val file3 = File("$separateSolutionsDir/$student3.txt")

    private val file10Name = "file9.java"
    private val file11Name = "file9.java"

    private val testPreparedAnalysisFiles = PreparedAnalysisData(
        GitProperty.GITHUB,
        testRepoName,
        Language.JAVA,
        listOf(base1, base2),
        listOf(
            Solution(
                student1, "", file1,
                includedFileNames = listOf(file1Name, file2Name, file3Name, file4Name),
                includedFilePositions = listOf(29, 55, 68, 76),
                sha = sha1
            ),
            Solution(
                student2, "", file2,
                includedFileNames = listOf(file5Name, file6Name, file7Name, file8Name),
                includedFilePositions = listOf(31, 62, 76, 84),
                sha = sha2
            ),
            Solution(
                student3, "", file3,
                includedFileNames = listOf(file9Name, file10Name, file11Name),
                includedFilePositions = listOf(12, 32, 50),
                sha = sha3
            )
        )
    )

    private val analysisService = MossAnalyser(solutionStorage, System.getenv("MOSS_ID"))

    private val expectedResult =
        AnalysisResult(
            repo = testRepoName,
            gitService = GitProperty.GITHUB,
            resultLink = "",
            executionDate = LocalDateTime.now(),
            matchData = listOf(
                AnalysisMatch(
                    students = student1 to student2,
                    sha = sha1 to sha2,
                    lines = 44,
                    percentage = 51,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 3 to 25,
                            match2 = 1 to 23,
                            files = file1Name to file5Name
                        ),
                        MatchedLines(
                            match1 = 23 to 27,
                            match2 = 26 to 32,
                            files = file2Name to file6Name
                        ),
                        MatchedLines(
                            match1 = 1 to 13,
                            match2 = 1 to 13,
                            files = file3Name to file7Name
                        ),
                        MatchedLines(
                            match1 = 1 to 1,
                            match2 = 1 to 1,
                            files = file4Name to file8Name
                        )
                    )
                ), AnalysisMatch(
                    students = student1 to student3,
                    sha = sha1 to sha3,
                    lines = 21,
                    percentage = 25,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 6 to 14,
                            match2 = 3 to 10,
                            files = file2Name to file11Name
                        ),
                        MatchedLines(
                            match1 = 15 to 19,
                            match2 = 11 to 16,
                            files = file2Name to file11Name
                        ),
                        MatchedLines(
                            match1 = 21 to 26,
                            match2 = 6 to 11,
                            files = file2Name to file9Name
                        ),
                        MatchedLines(
                            match1 = 1 to 1,
                            match2 = 1 to 1,
                            files = file3Name to file10Name
                        )
                    )
                ), AnalysisMatch(
                    students = student2 to student3,
                    sha = sha2 to sha3,
                    lines = 22,
                    percentage = 22,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 19 to 31,
                            match2 = 8 to 20,
                            files = file6Name to file8Name
                        ),
                        MatchedLines(
                            match1 = 1 to 4,
                            match2 = 1 to 4,
                            files = file7Name to file11Name
                        ),
                        MatchedLines(
                            match1 = 15 to 19,
                            match2 = 11 to 15,
                            files = file8Name to file11Name
                        )
                    )
                )
            )
        )

    @Test
    @Ignore
    fun analyse() {
        `when`(solutionStorage.loadBasesAndComposedSolutions(any(), any())).thenReturn(testPreparedAnalysisFiles)
        val analysisResult = analysisService.analyse(analysisSettings)
        analysisResult shouldEqual expectedResult.copy(
            resultLink = analysisResult.resultLink,
            executionDate = analysisResult.executionDate
        )
    }
}