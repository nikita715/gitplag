package ru.nikstep.redink.git.loader

import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty
import java.time.LocalDateTime

class GitlabLoaderTest : AbstractGitLoaderTest() {
    override val repoName = "testns3/plagiarism_test"
    override val pullRequest = PullRequest(
        number = 1,
        creatorName = "testns3",
        sourceRepoId = 11158542,
        mainRepoFullName = repoName,
        headSha = "6e12a8d031574dc6a07eaa9125d8ce4fdc2c9776",
        sourceBranchName = branchName,
        gitService = GitProperty.GITLAB,
        date = LocalDateTime.now(),
        sourceRepoFullName = repoName,
        mainRepoId = 11158542,
        mainBranchName = "master"
    )
    override val loader = GitlabLoader(solutionStorage, repositoryRepository)
}