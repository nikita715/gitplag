package ru.nikstep.redink.analysis.loader

import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty

class GithubLoaderTest : AbstractGitLoaderTest() {
    override val repoName = "testns/plagiarism_test"

    override val pullRequest = PullRequest(
        number = 1,
        secretKey = "key",
        creatorName = "testns",
        repoId = 1,
        repoFullName = repoName,
        headSha = "",
        branchName = branchName,
        gitService = GitProperty.GITHUB
    )

    override val loader = GithubLoader(solutionStorage, repositoryRepository, authorizationService)
}