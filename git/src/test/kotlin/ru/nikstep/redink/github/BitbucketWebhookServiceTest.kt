package ru.nikstep.redink.github

import ru.nikstep.redink.github.webhook.BitbucketWebhookService
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty

class BitbucketWebhookServiceTest : AbstractWebhookServiceTest() {
    //    override val argument: ArgumentCaptor<PullRequest> = ArgumentCaptor.forClass(PullRequest::class.java)
    override val payload by lazy { readPayloadOf("bitbucket") }
    override val webhookService = BitbucketWebhookService(pullRequestRepository)
    override val pullRequest = PullRequest(
        number = 3,
        secretKey = "",
        creatorName = "testns2",
        repoId = -1,
        repoFullName = "nikita715/plagiarism_test2",
        headSha = "1458a8caab0b",
        branchName = "testns2/javacljava-created-online-with-bitbucket-1551121394025",
        gitService = GitProperty.BITBUCKET
    )
}