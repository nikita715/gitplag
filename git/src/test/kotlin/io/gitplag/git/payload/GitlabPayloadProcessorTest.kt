package io.gitplag.git.payload

import io.gitplag.git.agent.GitlabAgent
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import io.kotlintest.mock.mock
import java.time.LocalDateTime
import java.time.Month

/**
 * Gitlab payload processor test implementation
 */
class GitlabPayloadProcessorTest : AbstractPayloadProcessorTest() {
    override val payload by lazy { readPayloadOf("gitlab") }

    override val gitAgent = mock<GitlabAgent>()

    override val repo = Repository(
        name = "nikita715/plagiarism_test3",
        gitService = GitProperty.GITLAB,
        language = Language.JAVA,
        gitId = ""
    )

    override val payloadProcessor =
        GitlabPayloadProcessor(pullRequestRepository, repositoryDataManager, gitAgent, branchRepository)

    override val pullRequest = PullRequest(
        number = 1,
        creatorName = "nikita715",
        sourceRepoFullName = "testns/plagiarism_test3",
        repo = repo,
        headSha = "d647870f53f333e3c0bec84cdd245e7262071331",
        sourceBranchName = "br2",
        mainBranchName = "master",
        createdAt = LocalDateTime.of(2019, Month.FEBRUARY, 28, 19, 14, 37),
        updatedAt = LocalDateTime.of(2019, Month.FEBRUARY, 28, 22, 12, 32)
    )
}