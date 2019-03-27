package ru.nikstep.redink.git.webhook

import io.kotlintest.mock.mock
import ru.nikstep.redink.git.rest.GithubRestManager
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language
import java.time.LocalDateTime
import java.time.Month

/**
 * Github payload processor test implementation
 */
class GithubPayloadProcessorTest : AbstractPayloadProcessorTest() {
    override val payload by lazy { readPayloadOf("github") }

    override val gitRestManager = mock<GithubRestManager>()

    override val repo = Repository(
        name = "nikita715/plagiarism_test",
        gitService = GitProperty.GITHUB,
        language = Language.JAVA,
        branches = listOf("testns2-patch-1")
    )

    override val payloadProcessor = GithubPayloadProcessor(pullRequestRepository, repositoryRepository, gitRestManager)

    override val pullRequest = PullRequest(
        number = 8,
        creatorName = "nikita715",
        sourceRepoId = 155913197,
        mainRepoId = 12345,
        sourceRepoFullName = "testns/plagiarism_test",
        repo = repo,
        headSha = "6ec548da744248919dc753deac536722a46c31f1",
        sourceBranchName = "testns2-patch-1",
        mainBranchName = "master",
        date = LocalDateTime.of(2019, Month.FEBRUARY, 27, 21, 31, 43)
    )
}