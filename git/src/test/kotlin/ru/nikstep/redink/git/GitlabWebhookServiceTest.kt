package ru.nikstep.redink.git

import ru.nikstep.redink.git.webhook.GitlabWebhookService
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty
import java.time.LocalDateTime
import java.time.Month

class GitlabWebhookServiceTest : AbstractWebhookServiceTest() {
    override val payload by lazy { readPayloadOf("gitlab") }
    override val webhookService = GitlabWebhookService(pullRequestRepository, applicationEventPublisher)
    override val pullRequest = PullRequest(
        number = 1,
        secretKey = "",
        creatorName = "nikita715",
        sourceRepoId = 11083523,
        mainRepoFullName = "nikita715/plagiarism_test3",
        sourceHeadSha = "d647870f53f333e3c0bec84cdd245e7262071331",
        sourceBranchName = "br2",
        gitService = GitProperty.GITLAB,
        date = LocalDateTime.of(2019, Month.FEBRUARY, 28, 22, 12, 32)
    )
}