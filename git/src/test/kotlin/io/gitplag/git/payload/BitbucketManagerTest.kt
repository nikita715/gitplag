package io.gitplag.git.payload

import com.nhaarman.mockitokotlin2.mock
import io.gitplag.git.agent.BitbucketAgent
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import java.time.LocalDateTime
import java.time.Month

/**
 * Bitbucket payload processor test implementation
 */
class BitbucketManagerTest : AbstractGitManagerTest() {
    override val payload by lazy { readPayloadOf("bitbucket") }

    override val gitAgent = mock<BitbucketAgent>()

    override val repo = Repository(
        name = "nikita715/plagiarism_test2",
        gitService = GitProperty.BITBUCKET,
        language = Language.JAVA,
        gitId = ""
    )

    override val gitManager =
        BitbucketManager(pullRequestRepository, repositoryDataManager, gitAgent, branchRepository)

    override val pullRequest = PullRequest(
        number = 3,
        creatorName = "testns2",
        sourceRepoFullName = "testns/plagiarism_test2",
        repo = repo,
        headSha = "1458a8caab0b",
        sourceBranchName = "testns2/javacljava-created-online-with-bitbucket-1551121394025",
        mainBranchName = "master",
        createdAt = LocalDateTime.of(2019, Month.FEBRUARY, 25, 19, 3, 19),
        updatedAt = LocalDateTime.of(2019, Month.FEBRUARY, 25, 20, 54, 28)
    )
}