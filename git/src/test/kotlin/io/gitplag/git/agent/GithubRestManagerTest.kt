package io.gitplag.git.agent

import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import java.time.LocalDateTime

/**
 * Github rest test implementation
 */
class GithubRestManagerTest : AbstractGitAgentTest() {

    override val repo = Repository(
        name = "testns/plagiarism_test",
        gitService = GitProperty.GITHUB,
        language = Language.JAVA,
        gitId = ""
    )

    override val pullRequest = PullRequest(
        number = 1,
        creatorName = "testns",
        repo = repo,
        headSha = "0ab6c9f464e8e8f13a80859178d0886ee438385d",
        sourceBranchName = branchName,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        sourceRepoFullName = repo.name,
        mainBranchName = "master"
    )

    override val agent =
        GithubAgent(solutionStorage, solutionFileRecordRepository, "")
}