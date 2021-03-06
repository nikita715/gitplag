package io.gitplag.git.payload

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.gitplag.git.agent.GitAgent
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.BranchRepository
import io.gitplag.model.repo.PullRequestRepository
import io.gitplag.util.asPath
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.mock.`when`
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import java.nio.file.Paths

/**
 * Abstract test for the payload processors
 */
abstract class AbstractGitManagerTest {

    abstract val payload: String
    abstract val gitManager: GitManager
    abstract val pullRequest: PullRequest
    abstract val repo: Repository

    private val argument: ArgumentCaptor<PullRequest> = ArgumentCaptor.forClass(PullRequest::class.java)

    private object PullRequestAnswer : Answer<PullRequest> {
        override fun answer(invocation: InvocationOnMock?): PullRequest = invocation?.arguments?.get(0) as PullRequest
    }

    protected val pullRequestRepository = mock<PullRequestRepository> {
        `when`(it.save<PullRequest>(any())).thenAnswer(PullRequestAnswer)
    }

    abstract val gitAgent: GitAgent

    protected val repositoryDataManager = mock<RepositoryDataManager>()
    protected val branchRepository = mock<BranchRepository>()

    /**
     * Test saving and requesting of the download of a pull request
     */
    @Test
    fun saveNewPullRequest() {
        `when`(repositoryDataManager.findByGitServiceAndName(any(), any())).thenReturn(repo)
        gitManager.downloadSolutionsOfPullRequest(payload)
        verify(pullRequestRepository).save(argument.capture())
        argument.value shouldEqual pullRequest
        verify(gitAgent).clonePullRequest(pullRequest)
    }

    companion object {
        private val relSolutionsDir = asPath("src", "test", "resources", "payload")

        internal fun readPayloadOf(gitService: String): String =
            Paths.get(relSolutionsDir, "$gitService.json").toFile().readText()
    }
}
