package ru.nikstep.redink.analysis

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Test
import ru.nikstep.redink.analysis.solutions.SolutionStorageService
import ru.nikstep.redink.model.entity.PullRequest
import java.io.File

class JPlagAnalysisServiceTest {

    private val repoName = "nikita715/plagiarism_test"
    private val fileName = "qwe/javacl.java"

    lateinit var pullRequest: PullRequest

    val solutionStorageService = mock<SolutionStorageService> {
        on { getCountOfSolutionFiles(repoName, fileName) } doReturn 3
        on { loadAllBasesAndSolutions(any()) } doReturn listOf(
            PreparedAnalysisFiles(
                fileName,
                File(""),
                listOf(File(""), File(""), File(""))
            )
        )
    }
    val jPlagAnalysisService = JPlagAnalysisService(solutionStorageService)

    @Before
    fun setUp() {
        pullRequest = mock {
            on { repoFullName } doReturn repoName
            on { changedFiles } doReturn listOf(fileName)
        }

    }

    @Test
    fun analyse() {

        val analyse = jPlagAnalysisService.analyse(pullRequest)

    }
}