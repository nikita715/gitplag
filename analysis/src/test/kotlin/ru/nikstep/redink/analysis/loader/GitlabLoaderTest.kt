package ru.nikstep.redink.analysis.loader

import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.util.GitProperty

class GitlabLoaderTest : AbstractGitLoaderTest() {
    override val repoName = "testns3/plagiarism_test"
    override val pullRequest = PullRequest(
        number = 1,
        creatorName = "testns3",
        repoId = 11158542,
        repoFullName = repoName,
        headSha = "",
        branchName = branchName,
        gitService = GitProperty.GITLAB
    )
    override val loader = GitlabLoader(solutionStorage, repositoryRepository)
}