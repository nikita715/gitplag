package ru.nikstep.redink.analysis

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import ru.nikstep.redink.analysis.analyser.Analyser
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
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

    protected val student1 = "student1"
    protected val student2 = "student2"
    protected val student3 = "student3"

    private val masterBranch = "master"

    protected val base =
        Paths.get(relSolutionsDir, gitService.toString(), testRepoName, masterBranch, ".base", testFileName).toFile()
    protected val solution1 =
        Paths.get(relSolutionsDir, gitService.toString(), testRepoName, masterBranch, student1, testFileName).toFile()
    protected val solution2 =
        Paths.get(relSolutionsDir, gitService.toString(), testRepoName, masterBranch, student2, testFileName).toFile()
    protected val solution3 =
        Paths.get(relSolutionsDir, gitService.toString(), testRepoName, masterBranch, student3, testFileName).toFile()

    protected val sha1 = "sha1"
    protected val sha2 = "sha2"
    protected val sha3 = "sha3"

    private val pullRequest = mock<PullRequest> {
        on { mainRepoFullName } doReturn testRepoName
        on { creatorName } doReturn student1
        on { gitService } doReturn gitService
    }

    protected abstract val analysisService: Analyser

    protected abstract val expectedResult: AnalysisResult

    private val repository = mock<Repository> {
        on { gitService } doReturn GitProperty.GITHUB
        on { name } doReturn testRepoName
    }

    protected val analysisSettings = mock<AnalysisSettings> {
        on { gitService } doReturn GitProperty.GITHUB
        on { repository } doReturn repository
        on { language } doReturn Language.JAVA
        on { branch } doReturn "master"
        on { withLines } doReturn true
    }
}