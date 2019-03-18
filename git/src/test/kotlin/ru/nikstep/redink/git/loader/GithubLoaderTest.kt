package ru.nikstep.redink.git.loader

import org.junit.Ignore
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language
import java.time.LocalDateTime

@Ignore
class GithubLoaderTest : AbstractGitLoaderTest() {

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
        headSha = "",
        sourceBranchName = branchName,
        date = LocalDateTime.now(),
        sourceRepoFullName = repo.name,
        mainRepoId = 1,
        mainBranchName = "master"
    )

    override val loader =
        GithubLoader(solutionStorage)
}