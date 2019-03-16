package ru.nikstep.redink.git.webhook

import io.kotlintest.mock.mock
import ru.nikstep.redink.checks.github.AnalysisStatusCheckService
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty
import java.time.LocalDateTime
import java.time.Month

class GithubWebhookServiceTest : AbstractWebhookServiceTest() {
    override val payload by lazy { readPayloadOf("github") }

    private val analysisStatusCheckService = mock<AnalysisStatusCheckService>()

    override val webhookService =
        GithubWebhookService(
            analysisStatusCheckService, pullRequestRepository,
            applicationEventPublisher
        )

    override val pullRequest = PullRequest(
        gitService = GitProperty.GITHUB,
        number = 8,
        creatorName = "nikita715",
        sourceRepoId = 155913197,
        mainRepoId = 12345,
        sourceRepoFullName = "nikita715/plagiarism_test",
        mainRepoFullName = "nikita715/plagiarism_test",
        headSha = "6ec548da744248919dc753deac536722a46c31f1",
        sourceBranchName = "testns2-patch-1",
        mainBranchName = "master",
        secretKey = "123",
        date = LocalDateTime.of(2019, Month.FEBRUARY, 27, 21, 31, 43)
    )
}