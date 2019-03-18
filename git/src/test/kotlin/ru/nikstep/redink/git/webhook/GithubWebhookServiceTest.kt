package ru.nikstep.redink.git.webhook

import io.kotlintest.mock.mock
import ru.nikstep.redink.git.loader.GithubLoader
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language
import java.time.LocalDateTime
import java.time.Month

class GithubWebhookServiceTest : AbstractWebhookServiceTest() {
    override val payload by lazy { readPayloadOf("github") }

    override val gitLoader = mock<GithubLoader>()

    override val repo = Repository(
        name = "nikita715/plagiarism_test",
        gitService = GitProperty.GITHUB,
        language = Language.JAVA
    )

    override val webhookService = GithubWebhookService(pullRequestRepository, repositoryRepository, gitLoader)

    override val pullRequest = PullRequest(
        number = 8,
        creatorName = "nikita715",
        sourceRepoId = 155913197,
        mainRepoId = 12345,
        sourceRepoFullName = "nikita715/plagiarism_test",
        repo = repo,
        headSha = "6ec548da744248919dc753deac536722a46c31f1",
        sourceBranchName = "testns2-patch-1",
        mainBranchName = "master",
        date = LocalDateTime.of(2019, Month.FEBRUARY, 27, 21, 31, 43)
    )
}