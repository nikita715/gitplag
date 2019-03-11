package ru.nikstep.redink.analysis.loader

import org.junit.Ignore
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty
import java.time.LocalDateTime

@Ignore
class GithubLoaderTest : AbstractGitLoaderTest() {
    override val repoName = "testns/plagiarism_test"

    override val pullRequest = PullRequest(
        number = 1,
        secretKey = "key",
        creatorName = "testns",
        sourceRepoId = 1,
        mainRepoFullName = repoName,
        sourceHeadSha = "",
        sourceBranchName = branchName,
        gitService = GitProperty.GITHUB,
        date = LocalDateTime.now(),
        sourceRepoFullName = "",
        mainRepoId = 1,
        mainHeadSha = "",
        mainBranchName = ""
    )

    override val loader = GithubLoader(solutionStorage, repositoryRepository, authorizationService)
}