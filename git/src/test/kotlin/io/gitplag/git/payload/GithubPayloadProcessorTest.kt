package io.gitplag.git.payload

import io.gitplag.git.agent.GithubAgent
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import io.kotlintest.mock.mock
import java.time.LocalDateTime
import java.time.Month

/**
 * Github payload processor test implementation
 */
class GithubPayloadProcessorTest : AbstractPayloadProcessorTest() {
    override val payload by lazy { readPayloadOf("github") }

    override val gitAgent = mock<GithubAgent>()

    override val repo = Repository(
        name = "nikita715/plagiarism_test",
        gitService = GitProperty.GITHUB,
        language = Language.JAVA,
        gitId = ""
    )

    override val payloadProcessor =
        GithubPayloadProcessor(pullRequestRepository, repositoryDataManager, gitAgent, branchRepository)

    override val pullRequest = PullRequest(
        number = 8,
        creatorName = "nikita715",
        sourceRepoFullName = "testns/plagiarism_test",
        repo = repo,
        headSha = "6ec548da744248919dc753deac536722a46c31f1",
        sourceBranchName = "testns2-patch-1",
        mainBranchName = "master",
        createdAt = LocalDateTime.of(2019, Month.FEBRUARY, 25, 18, 36, 37),
        updatedAt = LocalDateTime.of(2019, Month.FEBRUARY, 27, 21, 31, 43)
    )
}