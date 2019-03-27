package ru.nikstep.redink.git.rest

import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language
import java.time.LocalDateTime

/**
 * Gitlab rest test implementation
 */
class GitlabRestManagerTest : AbstractGitRestManagerTest() {

    override val repo = Repository(
        name = "testns3/plagiarism_test",
        gitService = GitProperty.GITLAB,
        language = Language.JAVA
    )

    override val pullRequest = PullRequest(
        number = 1,
        creatorName = "testns3",
        sourceRepoId = 11158542,
        repo = repo,
        headSha = "6e12a8d031574dc6a07eaa9125d8ce4fdc2c9776",
        sourceBranchName = branchName,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
        sourceRepoFullName = repo.name,
        mainRepoId = 11158542,
        mainBranchName = "master"
    )
    override val restManager = GitlabRestManager(solutionStorage)
}