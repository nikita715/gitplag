package io.gitplag.analysis

import com.nhaarman.mockitokotlin2.any
import io.gitplag.analysis.analyzer.MossAnalyzer
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.data.AnalysisMatch
import io.gitplag.model.data.AnalysisResult
import io.gitplag.model.data.MatchedLines
import io.gitplag.model.data.PreparedAnalysisData
import io.gitplag.model.data.Solution
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import io.gitplag.util.asPath
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.mock.`when`
import org.junit.Test
import org.mockito.Mockito
import java.io.File
import java.nio.file.Paths
import java.time.LocalDateTime

/**
 * Moss tests
 */
class MossAnalyzerTest : AbstractAnalyzerTest() {

    private val solutionsDir = asPath("src", "test", "resources", "mosspreparedfiles")

    private val solutionStorage = Mockito.mock(SourceCodeStorage::class.java)

    private val file1 = File("$solutionsDir/$student1/$student1.txt")
    private val file2 = File("$solutionsDir/$student2/$student2.txt")
    private val file3 = File("$solutionsDir/$student3/$student3.txt")
    private val base1 =
        File("$solutionsDir/.base/0.java")
    private val base2 =
        File("$solutionsDir/.base/1.java")

    private val testPreparedAnalysisFiles = PreparedAnalysisData(
        GitProperty.GITHUB,
        testRepoName,
        Language.JAVA,
        solutionsDir,
        listOf(base1, base2),
        listOf(
            Solution(
                student1, file1.name, file1,
                sha = sha1,
                createdAt = createdAtList[0]
            ),
            Solution(
                student2, file2.name, file2,
                sha = sha2,
                createdAt = createdAtList[1]
            ),
            Solution(
                student3, file3.name, file3,
                sha = sha3,
                createdAt = createdAtList[2]
            )
        ),
        analysisParameters = ""
    )

    private val analysisService = MossAnalyzer(
        solutionStorage, randomGenerator,
        Paths.get(solutionsDir).toFile().absolutePath, System.getenv("GITPLAG_MOSS_PATH")
    )

    private val expectedResult =
        AnalysisResult(
            repo = testRepoName,
            resultLink = "",
            executionDate = LocalDateTime.now(),
            hash = hash,
            matchData = listOf(
                AnalysisMatch(
                    students = student1 to student3,
                    sha = sha1 to sha3,
                    lines = 102,
                    percentage = 82,
                    createdAt = createdAtList[0] to createdAtList[2],
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 1 to 91,
                            match2 = 40 to 130,
                            files = file1.name to file3.name
                        ),
                        MatchedLines(
                            match1 = 107 to 115,
                            match2 = 12 to 22,
                            files = file1.name to file3.name
                        )
                    )
                ), AnalysisMatch(
                    students = student2 to student3,
                    sha = sha2 to sha3,
                    lines = 48,
                    percentage = 55,
                    createdAt = createdAtList[1] to createdAtList[2],
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 44 to 70,
                            match2 = 104 to 130,
                            files = file2.name to file3.name
                        ),
                        MatchedLines(
                            match1 = 32 to 43,
                            match2 = 6 to 17,
                            files = file2.name to file3.name
                        ),
                        MatchedLines(
                            match1 = 6 to 14,
                            match2 = 24 to 31,
                            files = file2.name to file3.name
                        )
                    )
                ), AnalysisMatch(
                    students = student1 to student2,
                    sha = sha1 to sha2,
                    lines = 45,
                    percentage = 40,
                    createdAt = createdAtList[0] to createdAtList[1],
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 65 to 91,
                            match2 = 44 to 70,
                            files = file1.name to file2.name
                        ),
                        MatchedLines(
                            match1 = 100 to 117,
                            match2 = 6 to 23,
                            files = file1.name to file2.name
                        )
                    )
                )
            )
        )

    /**
     * Run a sample analysis
     */
    @Test
    fun analyze() {
        `when`(solutionStorage.loadBasesAndComposedSolutions(any(), any())).thenReturn(testPreparedAnalysisFiles)
        val analysisResult = analysisService.analyze(analysisSettings)
        analysisResult shouldEqual expectedResult.copy(
            resultLink = analysisResult.resultLink,
            executionDate = analysisResult.executionDate
        )
    }
}