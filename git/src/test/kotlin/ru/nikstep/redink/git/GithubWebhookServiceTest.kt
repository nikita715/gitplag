package ru.nikstep.redink.git

import io.kotlintest.mock.mock
import ru.nikstep.redink.checks.AnalysisStatusCheckService
import ru.nikstep.redink.git.webhook.GithubWebhookService
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty

class GithubWebhookServiceTest : AbstractWebhookServiceTest() {
    //    override val argument: ArgumentCaptor<PullRequest> = ArgumentCaptor.forClass(PullRequest::class.java)
    override val payload by lazy { readPayloadOf("github") }

    private val analysisStatusCheckService = mock<AnalysisStatusCheckService>()

    override val webhookService =
        GithubWebhookService(
            analysisStatusCheckService,
            pullRequestRepository
        )

    override val pullRequest = PullRequest(
        number = 8,
        secretKey = "123",
        creatorName = "testns2",
        repoId = -1,
        repoFullName = "nikita715/plagiarism_test",
        headSha = "6ec548da744248919dc753deac536722a46c31f1",
        branchName = "testns2-patch-1",
        gitService = GitProperty.GITHUB
    )
}