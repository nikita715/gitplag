package ru.nikstep.redink.git.webhook

import com.nhaarman.mockitokotlin2.mock
import ru.nikstep.redink.git.loader.BitbucketLoader
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language
import java.time.LocalDateTime
import java.time.Month

class BitbucketPayloadProcessorTest : AbstractPayloadProcessorTest() {
    override val payload by lazy { readPayloadOf("bitbucket") }

    override val gitLoader = mock<BitbucketLoader>()

    override val repo = Repository(
        name = "nikita715/plagiarism_test2",
        gitService = GitProperty.BITBUCKET,
        language = Language.JAVA
    )

    override val payloadProcessor = BitbucketPayloadProcessor(pullRequestRepository, repositoryRepository, gitLoader)

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