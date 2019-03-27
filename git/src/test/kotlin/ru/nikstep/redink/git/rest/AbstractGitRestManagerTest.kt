package ru.nikstep.redink.git.rest

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import ru.nikstep.redink.analysis.solutions.SourceCodeStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.asPath
import java.nio.file.Paths

/**
 * Abstract test for the rest managers
 */
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

    val solutionStorage = mock<SourceCodeStorage>()

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

    /**
     * Test pull request cloning from the gits
     */
    @Test
    fun loadFileText() {
        restManager.clonePullRequest(pullRequest)
        verify(solutionStorage).saveSolutionsFromDir(any(), eq(pullRequest))
    }
}