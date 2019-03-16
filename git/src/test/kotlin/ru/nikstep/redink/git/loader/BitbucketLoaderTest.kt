package ru.nikstep.redink.git.loader

import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty
import java.time.LocalDateTime

class BitbucketLoaderTest : AbstractGitLoaderTest() {
    override val repoName = "nikita715/plagiarism_test2"

    override val pullRequest = PullRequest(
        number = 4,
        creatorName = "nikita715",
        sourceRepoId = 1,
        mainRepoFullName = repoName,
        headSha = "738c283091cbca80bd3701cc206480f5567d74a7",
        sourceBranchName = branchName,
        gitService = GitProperty.BITBUCKET,
        date = LocalDateTime.now(),
        sourceRepoFullName = repoName,
        mainRepoId = 1,
        mainBranchName = "master"
    )

    override val loader = BitbucketLoader(solutionStorage, repositoryRepository)
}