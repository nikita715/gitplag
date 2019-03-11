package ru.nikstep.redink.git

import ru.nikstep.redink.git.webhook.BitbucketWebhookService
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty
import java.time.LocalDateTime
import java.time.Month

class BitbucketWebhookServiceTest : AbstractWebhookServiceTest() {
    override val payload by lazy { readPayloadOf("bitbucket") }
    override val webhookService = BitbucketWebhookService(pullRequestRepository, applicationEventPublisher)
    override val pullRequest = PullRequest(
        number = 3,
        secretKey = "",
        creatorName = "testns2",
        sourceRepoId = -1,
        mainRepoFullName = "nikita715/plagiarism_test2",
        sourceHeadSha = "1458a8caab0b",
        sourceBranchName = "testns2/javacljava-created-online-with-bitbucket-1551121394025",
        gitService = GitProperty.BITBUCKET,
        date = LocalDateTime.of(2019, Month.FEBRUARY, 25, 20, 54, 28)
    )
}