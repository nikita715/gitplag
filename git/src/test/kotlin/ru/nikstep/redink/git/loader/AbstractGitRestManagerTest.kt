package ru.nikstep.redink.git.loader

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.asPath
import java.nio.file.Paths

abstract class AbstractGitRestManagerTest {
    abstract val repo: Repository
    val branchName = "test"

    private val relSolutionsDir = asPath("src", "test", "resources", "loader")

    private val baseFileNamesToFileTexts = mapOf("class1.java" to "empty class1\n", "class2.java" to "empty class2\n")
    private val fileNamesToFileTexts = mapOf("class1.java" to "class1 content\n", "class2.java" to "class2 content\n")

    private val baseClass1 = Paths.get(relSolutionsDir, ".base", "class1.java").toFile()
    private val baseClass2 = Paths.get(relSolutionsDir, ".base", "class2.java").toFile()
    private val class1 = Paths.get(relSolutionsDir, "class1.java").toFile()
    private val class2 = Paths.get(relSolutionsDir, "class2.java").toFile()

    abstract val pullRequest: PullRequest

    val solutionStorage = mock<SolutionStorage>()

    val repositoryRepository = mock<RepositoryRepository>()

    abstract val restManager: GitRestManager

    @Before
    fun setUp() {
        `when`(
            repositoryRepository.findByGitServiceAndName(
                pullRequest.repo.gitService,
                pullRequest.repo.name
            )
        ).thenReturn(
            repo
        )
    }

    @Test
    fun loadFileText() {
        `when`(
            solutionStorage.loadBase(
                pullRequest.repo.gitService,
                pullRequest.repo.name,
                pullRequest.sourceBranchName,
                class1.name
            )
        ).thenReturn(
            baseClass1
        )
        `when`(
            solutionStorage.loadBase(
                pullRequest.repo.gitService,
                pullRequest.repo.name,
                pullRequest.sourceBranchName,
                class2.name
            )
        ).thenReturn(
            baseClass2
        )

        restManager.loadFilesOfCommit(pullRequest)

        fileNamesToFileTexts.entries.forEach {
            verify(solutionStorage).saveSolution(pullRequest, it.key, it.value)
        }

    }
}