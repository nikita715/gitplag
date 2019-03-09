package ru.nikstep.redink.analysis.loader

import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty
import java.time.LocalDateTime

class BitbucketLoaderTest : AbstractGitLoaderTest() {
    override val repoName = "nikita715/plagiarism_test2"

    override val pullRequest = PullRequest(
        number = 4,
        creatorName = "nikita715",
        repoId = 1,
        repoFullName = repoName,
        headSha = "",
        branchName = branchName,
        gitService = GitProperty.BITBUCKET,
        date = LocalDateTime.now()
    )

    override val loader = BitbucketLoader(solutionStorage, repositoryRepository)
}