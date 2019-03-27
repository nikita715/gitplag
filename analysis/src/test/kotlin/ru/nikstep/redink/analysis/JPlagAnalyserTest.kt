package ru.nikstep.redink.analysis

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import ru.nikstep.redink.analysis.analyser.JPlagAnalyser
import ru.nikstep.redink.analysis.solutions.SourceCodeStorage
import ru.nikstep.redink.model.data.AnalysisMatch
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.MatchedLines
import ru.nikstep.redink.model.data.PreparedAnalysisData
import ru.nikstep.redink.model.data.Solution
import ru.nikstep.redink.model.entity.JPlagReport
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language
import ru.nikstep.redink.model.repo.JPlagReportRepository
import ru.nikstep.redink.util.RandomGenerator
import ru.nikstep.redink.util.asPath
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime

/**
 * JPlag tests
 */
class JPlagAnalyserTest : AbstractAnalyserTest() {

    private val relSolutionsDir = asPath("src", "test", "resources", "separateSolutions")

    private val solutionsDir = Paths.get(relSolutionsDir).toFile().absolutePath

    private val solution1 = File("$relSolutionsDir/$student1/$file1Name")
    private val solution2 = File("$relSolutionsDir/$student1/$file2Name")
    private val solution3 = File("$relSolutionsDir/$student1/$file3Name")
    private val solution4 = File("$relSolutionsDir/$student2/$file4Name")
    private val solution5 = File("$relSolutionsDir/$student2/$file5Name")
    private val solution6 = File("$relSolutionsDir/$student2/$file6Name")
    private val solution7 = File("$relSolutionsDir/$student3/$file7Name")
    private val solution8 = File("$relSolutionsDir/$student3/$file8Name")
    private val solution9 = File("$relSolutionsDir/$student3/$file9Name")

    private val resultDir = Files.createTempDirectory("dir").toFile().absolutePath + "/"
    private val serverUrl = "url"

    private val prefix = "prefix"
    private val randomGenerator = mock<RandomGenerator> {
        on { randomAlphanumeric(10) } doReturn prefix
    }

    private val testPreparedAnalysisFiles = PreparedAnalysisData(
        GitProperty.GITHUB,
        testRepoName,
        Language.JAVA,
        listOf(base1, base2),
        listOf(
            Solution(student1, file1Name, solution1, sha = sha1),
            Solution(student1, file2Name, solution2, sha = sha1),
            Solution(student1, file3Name, solution3, sha = sha1),
            Solution(student2, file4Name, solution4, sha = sha2),
            Solution(student2, file5Name, solution5, sha = sha2),
            Solution(student2, file6Name, solution6, sha = sha2),
            Solution(student3, file7Name, solution7, sha = sha3),
            Solution(student3, file8Name, solution8, sha = sha3),
            Solution(student3, file9Name, solution9, sha = sha3)
        )
    )

    private val solutionStorageService = mock<SourceCodeStorage> {
        on { loadBasesAndSeparatedSolutions(any()) } doReturn testPreparedAnalysisFiles
    }

    private val jPlagReportRepository = mock<JPlagReportRepository>()

    private val analysisService =
        JPlagAnalyser(
            solutionStorageService,
            randomGenerator,
            jPlagReportRepository,
            solutionsDir,
            resultDir,
            serverUrl
        )

    private val expectedResult =
        AnalysisResult(
            repo = testRepoName,
            resultLink = "$serverUrl/jplagresult/$prefix/index.html",
            executionDate = LocalDateTime.now(),
            matchData = listOf(
                AnalysisMatch(
                    students = "student2" to "student1",
                    sha = sha2 to sha1,
                    lines = -1,
                    percentage = 66,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 1 to 22,
                            match2 = 1 to 21,
                            files = file6Name to file3Name
                        ),
                        MatchedLines(
                            match1 = 1 to 24,
                            match2 = 3 to 26,
                            files = file4Name to file1Name
                        )
                    )
                ), AnalysisMatch(
                    students = "student3" to "student2",
                    sha = sha3 to sha2,
                    lines = -1,
                    percentage = 54,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 7 to 20,
                            match2 = 18 to 31,
                            files = file8Name to file5Name
                        ),
                        MatchedLines(
                            match1 = 3 to 10,
                            match2 = 6 to 14,
                            files = file9Name to file6Name
                        ),
                        MatchedLines(
                            match1 = 10 to 18,
                            match2 = 15 to 22,
                            files = file9Name to file6Name
                        )
                    )
                ), AnalysisMatch(
                    sha = sha3 to sha1,
                    students = "student3" to "student1",
                    lines = -1,
                    percentage = 44,
                    matchedLines = listOf(
                        MatchedLines(
                            match1 = 6 to 11,
                            match2 = 21 to 26,
                            files = file7Name to file2Name
                        ),
                        MatchedLines(
                            match1 = 3 to 10,
                            match2 = 6 to 13,
                            files = file9Name to file3Name
                        ),
                        MatchedLines(
                            match1 = 10 to 18,
                            match2 = 14 to 21,
                            files = file9Name to file3Name
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
        val analysisResult = analysisService.analyse(analysisSettings)
//        analysisResult shouldEqual expectedResult.copy(executionDate = analysisResult.executionDate)
        verify(jPlagReportRepository).save(
            JPlagReport(
                createdAt = analysisResult.executionDate,
                hash = prefix
            )
        )
    }
}