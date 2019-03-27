package ru.nikstep.redink.git.loader

import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language
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
        date = LocalDateTime.now(),
        sourceRepoFullName = repo.name,
        mainRepoId = 1,
        mainBranchName = "master"
    )

    override val restManager = BitbucketRestManager(solutionStorage)
}