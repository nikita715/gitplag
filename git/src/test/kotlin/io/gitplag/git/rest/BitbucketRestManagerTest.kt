package io.gitplag.git.rest

import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import java.time.LocalDateTime

/**
 * Bitbucket rest test implementation
 */
class BitbucketRestManagerTest : AbstractGitRestManagerTest() {

    override val repo = Repository(
        name = "nikita715/plagiarism_test2",
        gitService = GitProperty.BITBUCKET,
        language = Language.JAVA
    )

    override val pullRequest = PullRequest(
        number = 4,
        creatorName = "nikita715",
        sourceRepoId = 1,
        repo = repo,
        headSha = "738c283091cbca80bd3701cc206480f5567d74a7",
        sourceBranchName = branchName,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        sourceRepoFullName = repo.name,
        mainRepoId = 1,
        mainBranchName = "master"
    )

    override val restManager = BitbucketRestManager(solutionStorage)
}