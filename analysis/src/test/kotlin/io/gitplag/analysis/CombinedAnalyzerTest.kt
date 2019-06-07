package io.gitplag.analysis

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.gitplag.analysis.analyzer.CombinedAnalyzer
import io.gitplag.analysis.analyzer.JPlagAnalyzer
import io.gitplag.analysis.analyzer.MossAnalyzer
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.data.*
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import io.gitplag.util.asPath
import io.kotlintest.matchers.shouldBe
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneOffset

class CombinedAnalyzerTest {

    private val solutionsDir = asPath("src", "test", "resources", "combinedanalysis")

    private val base1 =
        File("$solutionsDir/.base/0.java")
    private val base2 =
        File("$solutionsDir/.base/1.java")

    private val resultDir = Files.createTempDirectory("dir").toFile().absolutePath + "/"

    private val student1 = "student1"
    private val student2 = "student2"
    private val student3 = "student3"
    private val file1Name = "student1.java"
    private val file2Name = "student2.java"
    private val file3Name = "student3.java"
    private val solution1 = File("$solutionsDir/$student1/$file1Name")
    private val solution2 = File("$solutionsDir/$student2/$file2Name")
    private val solution3 = File("$solutionsDir/$student3/$file3Name")
    private val sha1 = "sha1"
    private val sha2 = "sha2"
    private val sha3 = "sha3"

    private val executionDate = LocalDateTime.now()
    private val createdAtList = (0L..2L).map { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }

    val testRepoName = "repo"
    private val testPreparedAnalysisFiles = PreparedAnalysisData(
        GitProperty.GITHUB,
        testRepoName,
        Language.JAVA,
        File(solutionsDir).absolutePath,
        listOf(base1, base2),
        listOf(
            Solution(student1, file1Name, solution1, sha = sha1, createdAt = createdAtList[0]),
            Solution(student2, file2Name, solution2, sha = sha2, createdAt = createdAtList[1]),
            Solution(student3, file3Name, solution3, sha = sha3, createdAt = createdAtList[2])
        )
    )

    private val solutionStorageService = mock<SourceCodeStorage> {
        on { loadBasesAndComposedSolutions(any(), any()) } doReturn testPreparedAnalysisFiles
    }

    private val analyzers = mapOf(
        AnalyzerProperty.MOSS to MossAnalyzer(
            solutionStorageService,
            Paths.get(solutionsDir).toFile().absolutePath,
            System.getenv("GITPLAG_MOSS_ID")
        ),
        AnalyzerProperty.JPLAG to JPlagAnalyzer(
            solutionStorageService,
            Paths.get(solutionsDir).toFile().absolutePath,
            resultDir
        )
    )

    private val repository = mock<Repository> {
        on { gitService } doReturn GitProperty.GITHUB
        on { name } doReturn testRepoName
    }

    private val analysisSettings = mock<AnalysisSettings> {
        on { repository } doReturn repository
        on { language } doReturn Language.JAVA
        on { branch } doReturn "master"
        on { analysisMode } doReturn AnalysisMode.FULL
        on { executionDate } doReturn executionDate
    }

    private val analysisService =
        CombinedAnalyzer(
            analyzers,
            solutionStorageService,
            resultDir
        )

    private val expectedResult =
        AnalysisResult(
            repo = testRepoName,
            resultLink = "",
            executionDate = executionDate,
            matchData = listOf(
                AnalysisMatch(
                    students = student1 to student2,
                    sha = sha1 to sha2,
                    percentage = 68,
                    minPercentage = 43,
                    maxPercentage = 97,
                    createdAt = createdAtList[0] to createdAtList[1],
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 12 to 21,
                            match2 = 13 to 22,
                            files = file1Name to file2Name
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
        val analysisResult = analysisService.analyze(analysisSettings)
        val executionDate = analysisResult.executionDate
        analysisResult shouldBe expectedResult.copy(
            executionDate = executionDate,
            resultLink = analysisResult.resultLink
        )
        File(resultDir).deleteRecursively()
    }
}