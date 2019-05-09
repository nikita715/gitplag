package io.gitplag.git.payload

import com.nhaarman.mockitokotlin2.mock
import io.gitplag.git.rest.BitbucketRestManager
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import java.time.LocalDateTime
import java.time.Month

/**
 * Bitbucket payload processor test implementation
 */
class BitbucketPayloadProcessorTest : AbstractPayloadProcessorTest() {
    override val payload by lazy { readPayloadOf("bitbucket") }

    override val gitRestManager = mock<BitbucketRestManager>()

    override val repo = Repository(
        name = "nikita715/plagiarism_test2",
        gitService = GitProperty.BITBUCKET,
        language = Language.JAVA
    )

    override val payloadProcessor =
        BitbucketPayloadProcessor(pullRequestRepository, repositoryDataManager, gitRestManager, branchRepository)

    override val pullRequest = PullRequest(
        number = 3,
        creatorName = "testns2",
        sourceRepoId = -1,
        mainRepoId = -1,
        sourceRepoFullName = "testns/plagiarism_test2",
        repo = repo,
        headSha = "1458a8caab0b",
        sourceBranchName = "testns2/javacljava-created-online-with-bitbucket-1551121394025",
        mainBranchName = "master",
        createdAt = LocalDateTime.of(2019, Month.FEBRUARY, 25, 19, 3, 19),
        updatedAt = LocalDateTime.of(2019, Month.FEBRUARY, 25, 20, 54, 28)
    )
}