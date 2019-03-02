package ru.nikstep.redink.github

import org.mockito.ArgumentCaptor
import ru.nikstep.redink.github.webhook.GitlabWebhookService
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty

class GitlabWebhookServiceTest : AbstractWebhookServiceTest() {
    override val argument: ArgumentCaptor<PullRequest> = ArgumentCaptor.forClass(PullRequest::class.java)
    override val bitbucketPayload by lazy { readPayloadOf("gitlab") }
    override val webhookService =
        GitlabWebhookService(pullRequestRepository, changeLoader)
    override val pullRequest = PullRequest(
        number = 1,
        secretKey = "",
        creatorName = "nikita715",
        repoId = 11083523,
        repoFullName = "nikita715/plagiarism_test3",
        headSha = "d647870f53f333e3c0bec84cdd245e7262071331",
        branchName = "br2",
        gitService = GitProperty.GITLAB
    )
}