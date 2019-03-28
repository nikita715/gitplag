package ru.nikstep.redink.analysis

import com.nhaarman.mockitokotlin2.any
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.mock.`when`
import org.junit.Test
import org.mockito.Mockito
import ru.nikstep.redink.analysis.analyser.MossAnalyser
import ru.nikstep.redink.analysis.solutions.SourceCodeStorage
import ru.nikstep.redink.model.data.*
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language
import ru.nikstep.redink.util.asPath
import java.io.File
import java.time.LocalDateTime

/**
 * Moss tests
 */
class MossAnalyserTest : AbstractAnalyserTest() {

    private val separateSolutionsDir = asPath("src", "test", "resources", "mosspreparedfiles")

    private val solutionStorage = Mockito.mock(SourceCodeStorage::class.java)

    private val file1 = File("$separateSolutionsDir/$student1.txt")
    private val file2 = File("$separateSolutionsDir/$student2.txt")
    private val file3 = File("$separateSolutionsDir/$student3.txt")

    private val file10Name = "file10.java"

    private val testPreparedAnalysisFiles = PreparedAnalysisData(
        GitProperty.GITHUB,
        testRepoName,
        Language.JAVA,
        listOf(base1, base2),
        listOf(
            Solution(
                student1, "", file1,
                includedFileNames = listOf(file1Name, file2Name, file3Name),
                includedFilePositions = listOf(68, 94, 120),
                sha = sha1
            ),
            Solution(
                student2, "", file2,
                includedFileNames = listOf(file4Name, file5Name, file6Name),
                includedFilePositions = listOf(26, 47, 77),
                sha = sha2
            ),
            Solution(
                student3, "", file3,
                includedFileNames = listOf(file7Name, file8Name, file9Name, file10Name),
                includedFilePositions = listOf(21, 39, 107, 133),
                sha = sha3
            )
        )
    )

    private val analysisService = MossAnalyser(solutionStorage, System.getenv("MOSS_ID"))

    private val expectedResult =
        AnalysisResult(
            repo = testRepoName,
            resultLink = "",
            executionDate = LocalDateTime.now(),
            matchData = listOf(
                AnalysisMatch(
                    students = student1 to student3,
                    sha = sha1 to sha3,
                    lines = 102,
                    percentage = 82,
                    matchedLines = listOf(
//                        MatchedLines(
//                            match1 = 1 to 91,
//                            match2 = 1 to 91,
//                            files = file1Name to file9Name
//                        ),
//                        MatchedLines(
//                            match1 = 13 to 21,
//                            match2 = 12 to 22,
//                            files = file3Name to file7Name
//                        )
                    )
                ), AnalysisMatch(
                    students = student2 to student3,
                    sha = sha2 to sha3,
                    lines = 48,
                    percentage = 55,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 18 to 21,
                            match2 = 65 to 68,
                            files = file5Name to file9Name
                        ),
                        MatchedLines(
                            match1 = 1 to 23,
                            match2 = 1 to 23,
                            files = file6Name to file10Name
                        ),
                        MatchedLines(
                            match1 = 6 to 17,
                            match2 = 6 to 17,
                            files = file5Name to file7Name
                        ),
                        MatchedLines(
                            match1 = 6 to 14,
                            match2 = 3 to 10,
                            files = file4Name to file8Name
                        )
                    )
                ), AnalysisMatch(
                    students = student1 to student2,
                    sha = sha1 to sha2,
                    lines = 45,
                    percentage = 40,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 65 to 68,
                            match2 = 18 to 21,
                            files = file1Name to file5Name
                        ),
                        MatchedLines(
                            match1 = 69 to 91,
                            match2 = 1 to 23,
                            files = file2Name to file6Name
                        ),
                        MatchedLines(
                            match1 = 6 to 23,
                            match2 = 6 to 23,
                            files = file3Name to file4Name
                        )
                    )
                )
            )
        )

    /**
     * Run a sample analysis
     */
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