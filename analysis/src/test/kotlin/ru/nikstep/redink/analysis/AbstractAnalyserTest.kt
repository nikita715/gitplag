package ru.nikstep.redink.analysis

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.kotlintest.matchers.shouldEqual
import org.junit.Test
import ru.nikstep.redink.analysis.analyser.Analyser
import ru.nikstep.redink.analysis.data.AnalysisSettings
import ru.nikstep.redink.analysis.data.CommittedFile
import ru.nikstep.redink.analysis.data.PreparedAnalysisFiles
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language
import ru.nikstep.redink.util.asPath
import java.nio.file.Paths

abstract class AbstractAnalyserTest {

    protected val testRepoName = "nikita715/plagiarism_test"
    protected val testFileName = "dir/FileTest.java"

    protected val gitService = GitProperty.GITHUB

    private val relSolutionsDir = asPath("src", "test", "resources", "test_solutions")

    internal val solutionsDir = Paths.get(relSolutionsDir).toFile().absolutePath

    private val student1 = "student1"
    private val student2 = "student2"
    private val student3 = "student3"

    private val bases =
        listOf(Paths.get(relSolutionsDir, gitService.toString(), testRepoName, ".base", testFileName).toFile())
    private val solution1 =
        Paths.get(relSolutionsDir, gitService.toString(), testRepoName, student1, testFileName).toFile()
    private val solution2 =
        Paths.get(relSolutionsDir, gitService.toString(), testRepoName, student2, testFileName).toFile()
    private val solution3 =
        Paths.get(relSolutionsDir, gitService.toString(), testRepoName, student3, testFileName).toFile()
    protected val sha1 = "sha1"
    protected val sha2 = "sha2"
    protected val sha3 = "sha3"

    private val testPreparedAnalysisFiles = PreparedAnalysisFiles(
        testRepoName,
        Language.JAVA,
        AnalyserProperty.JPLAG,
        bases,
        mapOf(
            student1 to CommittedFile(solution1, sha1, testFileName),
            student2 to CommittedFile(solution2, sha2, testFileName),
            student3 to CommittedFile(solution3, sha3, testFileName)
        )
    )

    internal val solutionStorageService = mock<SolutionStorage> {
        on { getCountOfSolutionFiles(repository, testFileName) } doReturn 3
        on { loadAllBasesAndSolutions(any()) } doReturn testPreparedAnalysisFiles
    }

    private val pullRequest = mock<PullRequest> {
        on { repoFullName } doReturn testRepoName
        on { creatorName } doReturn student1
        on { gitService } doReturn gitService
    }

    protected abstract val analysisService: Analyser

    protected abstract val expectedResult: List<AnalysisResult>

    private val repository = mock<Repository> {
        on { gitService } doReturn GitProperty.GITHUB
        on { name } doReturn testRepoName
    }

    private val analysisSettings = mock<AnalysisSettings> {
        on { gitService } doReturn GitProperty.GITHUB
        on { repository } doReturn repository
        on { language } doReturn Language.JAVA
    }

    @Test
    fun analyse() {
        analysisService.analyse(analysisSettings) shouldEqual expectedResult
    }
}