package ru.nikstep.redink.git.loader

import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language
import java.time.LocalDateTime

class GitlabLoaderTest : AbstractGitLoaderTest() {

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
        date = LocalDateTime.now(),
        sourceRepoFullName = repo.name,
        mainRepoId = 11158542,
        mainBranchName = "master"
    )
    override val loader = GitlabLoader(solutionStorage)
}