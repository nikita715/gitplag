package ru.nikstep.redink.git.webhook

import com.nhaarman.mockitokotlin2.mock
import ru.nikstep.redink.git.loader.BitbucketRestManager
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language
import java.time.LocalDateTime
import java.time.Month

class BitbucketPayloadProcessorTest : AbstractPayloadProcessorTest() {
    override val payload by lazy { readPayloadOf("bitbucket") }

    override val gitRestManager = mock<BitbucketRestManager>()

    override val repo = Repository(
        name = "nikita715/plagiarism_test2",
        gitService = GitProperty.BITBUCKET,
        language = Language.JAVA,
        branches = listOf("testns2/javacljava-created-online-with-bitbucket-1551121394025")
    )

    override val payloadProcessor =
        BitbucketPayloadProcessor(pullRequestRepository, repositoryRepository, gitRestManager)

    override val pullRequest = PullRequest(
        number = 3,
        creatorName = "testns2",
        sourceRepoId = -1,
        mainRepoId = -1,
        sourceRepoFullName = "nikita715/plagiarism_test2",
        repo = repo,
        headSha = "1458a8caab0b",
        sourceBranchName = "testns2/javacljava-created-online-with-bitbucket-1551121394025",
        mainBranchName = "master",
        date = LocalDateTime.of(2019, Month.FEBRUARY, 25, 20, 54, 28)
    )
}