package ru.nikstep.redink.git.loader

import org.junit.Ignore
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language
import java.time.LocalDateTime

@Ignore
class BitbucketLoaderTest : AbstractGitLoaderTest() {

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

    override val loader = BitbucketLoader(solutionStorage)
}