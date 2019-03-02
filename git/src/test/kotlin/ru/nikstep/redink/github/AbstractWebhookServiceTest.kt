package ru.nikstep.redink.github

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.mock.`when`
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository

abstract class AbstractWebhookServiceTest {

    abstract val bitbucketPayload: String

    abstract val webhookService: WebhookService

    abstract val pullRequest: PullRequest

    abstract val argument: ArgumentCaptor<PullRequest>

    private object PullRequestAnswer : Answer<PullRequest> {
        override fun answer(invocation: InvocationOnMock?): PullRequest = invocation!!.arguments[0] as PullRequest
    }

    protected val pullRequestRepository = mock<PullRequestRepository> {
        `when`(it.save<PullRequest>(any())).thenAnswer(PullRequestAnswer)
    }

    @Test
    fun saveNewPullRequest() {
        webhookService.saveNewPullRequest(bitbucketPayload)
        verify(pullRequestRepository).save(argument.capture())
        argument.value shouldEqual pullRequest
    }
}