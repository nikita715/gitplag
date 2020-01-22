package io.gitplag.analysis

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.gitplag.analysis.analyzer.JPlagAnalyzer
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.gitplagapi.model.enums.AnalyzerProperty
import io.gitplag.model.data.AnalysisMatch
import io.gitplag.model.data.AnalysisResult
import io.gitplag.model.data.MatchedLines
import io.gitplag.model.data.PreparedAnalysisData
import io.gitplag.model.data.Solution
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import io.gitplag.util.asPath
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * JPlag tests
 */
class JPlagAnalyzerTest : AbstractAnalyzerTest() {

    private val solutionsDir = asPath("src", "test", "resources", "jplagpreparedfiles")

    private val base1 =
        File("$solutionsDir/.base/0.java")
    private val base2 =
        File("$solutionsDir/.base/1.java")

    private val solution1 = File("$solutionsDir/$student1/$file1Name")
    private val solution2 = File("$solutionsDir/$student1/$file2Name")
    private val solution3 = File("$solutionsDir/$student1/$file3Name")
    private val solution4 = File("$solutionsDir/$student2/$file4Name")
    private val solution5 = File("$solutionsDir/$student2/$file5Name")
    private val solution6 = File("$solutionsDir/$student2/$file6Name")
    private val solution7 = File("$solutionsDir/$student3/$file7Name")
    private val solution8 = File("$solutionsDir/$student3/$file8Name")
    private val solution9 = File("$solutionsDir/$student3/$file9Name")

    private val resultDir = Files.createTempDirectory("dir").toFile().absolutePath + "/"

    private val testPreparedAnalysisFiles = PreparedAnalysisData(
        GitProperty.GITHUB,
        testRepoName,
        Language.JAVA,
        File(solutionsDir).absolutePath,
        listOf(base1, base2),
        listOf(
            Solution(student1, file1Name, solution1, sha = sha1, createdAt = createdAtList[0]),
            Solution(student1, file2Name, solution2, sha = sha1, createdAt = createdAtList[0]),
            Solution(student1, file3Name, solution3, sha = sha1, createdAt = createdAtList[0]),
            Solution(student2, file4Name, solution4, sha = sha2, createdAt = createdAtList[1]),
            Solution(student2, file5Name, solution5, sha = sha2, createdAt = createdAtList[1]),
            Solution(student2, file6Name, solution6, sha = sha2, createdAt = createdAtList[1]),
            Solution(student3, file7Name, solution7, sha = sha3, createdAt = createdAtList[2]),
            Solution(student3, file8Name, solution8, sha = sha3, createdAt = createdAtList[2]),
            Solution(student3, file9Name, solution9, sha = sha3, createdAt = createdAtList[2])
        ),
        null,
        null
    )

    private val solutionStorageService = mock<SourceCodeStorage> {
        on { loadBasesAndSolutions(any(), any()) } doReturn testPreparedAnalysisFiles
    }

    private val analysisService =
        JPlagAnalyzer(
            solutionStorageService,
            Paths.get(solutionsDir).toFile().absolutePath,
            resultDir
        )

    private val expectedResult =
        AnalysisResult(
            repo = testRepoName,
            resultLink = "",
            executionDate = executionDate,
            matchData = listOf(
                AnalysisMatch(
                    students = "student1" to "student2",
                    sha = sha1 to sha2,
                    percentage = 67,
                    minPercentage = 67,
                    maxPercentage = 67,
                    createdAt = createdAtList[0] to createdAtList[1],
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 12 to 21,
                            match2 = 13 to 22,
                            files = file3Name to file6Name,
                            analyzer = AnalyzerProperty.JPLAG
                        ),
                        MatchedLines(
                            match1 = 3 to 26,
                            match2 = 1 to 24,
                            files = file1Name to file4Name,
                            analyzer = AnalyzerProperty.JPLAG
                        ),
                        MatchedLines(
                            match1 = 1 to 11,
                            match2 = 1 to 11,
                            files = file3Name to file5Name,
                            analyzer = AnalyzerProperty.JPLAG
                        )
                    )
                ), AnalysisMatch(
                    students = "student2" to "student3",
                    sha = sha2 to sha3,
                    percentage = 42,
                    minPercentage = 42,
                    maxPercentage = 42,
                    createdAt = createdAtList[1] to createdAtList[2],
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 18 to 31,
                            match2 = 7 to 20,
                            files = file5Name to file8Name,
                            analyzer = AnalyzerProperty.JPLAG
                        ),
                        MatchedLines(
                            match1 = 15 to 22,
                            match2 = 10 to 18,
                            files = file6Name to file9Name,
                            analyzer = AnalyzerProperty.JPLAG
                        )
                    )
                ), AnalysisMatch(
                    sha = sha1 to sha3,
                    students = "student1" to "student3",
                    percentage = 43,
                    minPercentage = 43,
                    maxPercentage = 43,
                    createdAt = createdAtList[0] to createdAtList[2],
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 21 to 26,
                            match2 = 6 to 11,
                            files = file2Name to file7Name,
                            analyzer = AnalyzerProperty.JPLAG
                        ),
                        MatchedLines(
                            match1 = 6 to 13,
                            match2 = 3 to 10,
                            files = file3Name to file9Name,
                            analyzer = AnalyzerProperty.JPLAG
                        ),
                        MatchedLines(
                            match1 = 14 to 21,
                            match2 = 10 to 18,
                            files = file3Name to file9Name,
                            analyzer = AnalyzerProperty.JPLAG
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
//        analysisResult shouldBe expectedResult.copy(
//            executionDate = executionDate,
//            resultLink = "/jplagresult/${analysisFilesDirectoryName(analysisSettings)}/index.html"
//        )
    }
}