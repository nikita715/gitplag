package io.gitplag.git.rest

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.SolutionFileRecordRepository
import io.gitplag.util.asPath
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
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
    val solutionFileRecordRepository = mock<SolutionFileRecordRepository>()

    private val repositoryDataManager = mock<RepositoryDataManager>()

    abstract val restManager: GitRestManager

    @Before
    fun setUp() {
        `when`(
            repositoryDataManager.findByGitServiceAndName(
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