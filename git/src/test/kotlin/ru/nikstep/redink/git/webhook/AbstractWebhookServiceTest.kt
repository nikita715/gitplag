package ru.nikstep.redink.git.webhook

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.mock.`when`
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import ru.nikstep.redink.git.loader.GitLoader
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.asPath
import java.nio.file.Paths

abstract class AbstractWebhookServiceTest {

    abstract val payload: String
    abstract val webhookService: WebhookService
    abstract val pullRequest: PullRequest
    abstract val repo: Repository

    private val argument: ArgumentCaptor<PullRequest> = ArgumentCaptor.forClass(PullRequest::class.java)

    private object PullRequestAnswer : Answer<PullRequest> {
        override fun answer(invocation: InvocationOnMock?): PullRequest = invocation!!.arguments[0] as PullRequest
    }

    protected val pullRequestRepository = mock<PullRequestRepository> {
        `when`(it.save<PullRequest>(any())).thenAnswer(PullRequestAnswer)
    }

    protected val gitLoader = mock<GitLoader>()

    protected val repositoryRepository = mock<RepositoryRepository> {
        on { findByGitServiceAndName(pullRequest.repo.gitService, pullRequest.repo.name) } doReturn repo
    }

    @Test
    fun saveNewPullRequest() {
        webhookService.saveNewPullRequest(payload)
        verify(pullRequestRepository).save(argument.capture())
        argument.value shouldEqual pullRequest
        verify(gitLoader).loadFilesOfCommit(eq(pullRequest))
    }

    companion object {
        private val relSolutionsDir = asPath("src", "test", "resources", "payload")

        internal fun readPayloadOf(gitService: String): String =
            Paths.get(relSolutionsDir, "$gitService.json").toFile().readText()
    }
}
