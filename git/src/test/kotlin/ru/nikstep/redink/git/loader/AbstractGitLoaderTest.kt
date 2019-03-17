package ru.nikstep.redink.git.loader

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.asPath
import ru.nikstep.redink.util.auth.AuthorizationService
import java.io.File
import java.nio.file.Paths

abstract class AbstractGitLoaderTest {
    abstract val repoName: String
    val branchName = "test"

    private val relSolutionsDir = asPath("src", "test", "resources", "loader")

    private val baseFileNamesToFileTexts = mapOf("class1.java" to "empty class1\n", "class2.java" to "empty class2\n")
    private val fileNamesToFileTexts = mapOf("class1.java" to "class1 content\n", "class2.java" to "class2 content\n")

    private val baseClass1 = Paths.get(relSolutionsDir, ".base", "class1.java").toFile()
    private val baseClass2 = Paths.get(relSolutionsDir, ".base", "class2.java").toFile()
    private val class1 = Paths.get(relSolutionsDir, "class1.java").toFile()
    private val class2 = Paths.get(relSolutionsDir, "class2.java").toFile()

    private val repository = mock<Repository> {
        on { name } doReturn repoName
        on { it.filePatterns } doReturn fileNamesToFileTexts.keys
    }

    abstract val pullRequest: PullRequest

    val solutionStorage = mock<SolutionStorage>()

    val repositoryRepository = mock<RepositoryRepository>()

    val authorizationService = mock<AuthorizationService>()

    abstract val loader: GitLoader

    @Before
    fun setUp() {
        `when`(
            repositoryRepository.findByGitServiceAndName(
                pullRequest.gitService,
                pullRequest.mainRepoFullName
            )
        ).thenReturn(
            repository
        )
        `when`(authorizationService.getAuthorizationToken(pullRequest.secretKey)).thenReturn("")
    }

    @Test
    fun loadFileTextAndBases() {
        `when`(
            solutionStorage.loadBase(
                pullRequest.gitService,
                pullRequest.mainRepoFullName,
                pullRequest.sourceBranchName,
                class1.name
            )
        ).thenReturn(
            File(
                ""
            )
        )
        `when`(
            solutionStorage.loadBase(
                pullRequest.gitService,
                pullRequest.mainRepoFullName,
                pullRequest.sourceBranchName,
                class2.name
            )
        ).thenReturn(
            File(
                ""
            )
        )

        loader.loadFilesOfPullRequest(pullRequest)

        baseFileNamesToFileTexts.entries.forEach {
            verify(solutionStorage).saveBase(pullRequest, it.key, it.value)
        }

        fileNamesToFileTexts.entries.forEach {
            verify(solutionStorage).saveSolution(pullRequest, it.key, it.value)
        }

    }

    @Test
    fun loadFileTextWithoutBases() {
        `when`(
            solutionStorage.loadBase(
                pullRequest.gitService,
                pullRequest.mainRepoFullName,
                pullRequest.sourceBranchName,
                class1.name
            )
        ).thenReturn(
            baseClass1
        )
        `when`(
            solutionStorage.loadBase(
                pullRequest.gitService,
                pullRequest.mainRepoFullName,
                pullRequest.sourceBranchName,
                class2.name
            )
        ).thenReturn(
            baseClass2
        )

        loader.loadFilesOfPullRequest(pullRequest)

        verify(solutionStorage, never()).saveBase(eq(pullRequest), any(), any())

        fileNamesToFileTexts.entries.forEach {
            verify(solutionStorage).saveSolution(pullRequest, it.key, it.value)
        }

    }
}