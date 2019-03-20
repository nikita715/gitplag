package ru.nikstep.redink.git.webhook

import com.nhaarman.mockitokotlin2.any
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

    abstract val gitLoader: GitLoader

    protected val repositoryRepository = mock<RepositoryRepository>()

    @Test
    fun saveNewPullRequest() {
        `when`(repositoryRepository.findByGitServiceAndName(any(), any())).thenReturn(repo)
        webhookService.updateSolutionsOfPullRequest(payload)
        verify(pullRequestRepository).save(argument.capture())
        argument.value shouldEqual pullRequest
        verify(gitLoader).loadFilesOfCommit(pullRequest)
    }

    companion object {
        private val relSolutionsDir = asPath("src", "test", "resources", "payload")

        internal fun readPayloadOf(gitService: String): String =
            Paths.get(relSolutionsDir, "$gitService.json").toFile().readText()
    }
}
