package ru.nikstep.redink.analysis

import com.nhaarman.mockitokotlin2.any
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.mock.`when`
import org.junit.Test
import org.mockito.Mockito
import ru.nikstep.redink.analysis.analyser.MossAnalyser
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.*
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

    private val testPreparedAnalysisFiles = PreparedAnalysisData(
        GitProperty.GITHUB,
        testRepoName,
        Language.JAVA,
        listOf(base1, base2),
        listOf(
            Solution(
                student1, "", file1,
                includedFileNames = listOf(file1Name, file2Name, file3Name),
                includedFilePositions = listOf(12, 32, 53),
                sha = sha1
            ),
            Solution(
                student2, "", file2,
                includedFileNames = listOf(file4Name, file5Name, file6Name),
                includedFilePositions = listOf(31, 62, 84),
                sha = sha2
            ),
            Solution(
                student3, "", file3,
                includedFileNames = listOf(file7Name, file8Name, file9Name),
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
                            files = file1Name to file4Name
                        ),
                        MatchedLines(
                            match1 = 19 to 36,
                            match2 = 25 to 45,
                            files = file3Name to file5Name
                        )
                    )
                ), AnalysisMatch(
                    students = student1 to student3,
                    sha = sha1 to sha3,
                    lines = 21,
                    percentage = 25,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 2 to 10,
                            match2 = 2 to 9,
                            files = file3Name to file9Name
                        ),
                        MatchedLines(
                            match1 = 17 to 23,
                            match2 = 6 to 12,
                            files = file3Name to file7Name
                        ),
                        MatchedLines(
                            match1 = 11 to 15,
                            match2 = 10 to 14,
                            files = file3Name to file9Name
                        )
                    )
                ), AnalysisMatch(
                    students = student2 to student3,
                    sha = sha2 to sha3,
                    lines = 22,
                    percentage = 22,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 18 to 34,
                            match2 = 7 to 23,
                            files = file5Name to file8Name
                        ),
                        MatchedLines(
                            match1 = 14 to 18,
                            match2 = 10 to 14,
                            files = file6Name to file9Name
                        )
                    )
                )
            )
        )

    @Test
    fun analyse() {
        `when`(solutionStorage.loadBasesAndComposedSolutions(any(), any())).thenReturn(testPreparedAnalysisFiles)
        val analysisResult = analysisService.analyse(analysisSettings)
        analysisResult shouldEqual expectedResult.copy(
            resultLink = analysisResult.resultLink,
            executionDate = analysisResult.executionDate
        )
    }
}