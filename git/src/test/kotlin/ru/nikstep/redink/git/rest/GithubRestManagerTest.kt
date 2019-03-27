package ru.nikstep.redink.git.rest

import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language
import java.time.LocalDateTime

/**
 * Github rest test implementation
 */
class GithubRestManagerTest : AbstractGitRestManagerTest() {

    override val repo = Repository(
        name = "testns/plagiarism_test",
        gitService = GitProperty.GITHUB,
        language = Language.JAVA
    )

    override val pullRequest = PullRequest(
        number = 1,
        creatorName = "testns",
        sourceRepoId = 1,
        repo = repo,
        headSha = "0ab6c9f464e8e8f13a80859178d0886ee438385d",
        sourceBranchName = branchName,
        date = LocalDateTime.now(),
        sourceRepoFullName = repo.name,
        mainRepoId = 1,
        mainBranchName = "master"
    )

    override val restManager =
        GithubRestManager(solutionStorage)
}