package ru.nikstep.redink.analysis.loader

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Ignore
import org.junit.Test
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.auth.AuthorizationService

class GithubLoaderTest {

    private val repoName = "testns/plagiarism_test"
    private val branchName = "test"

    private val pullRequest = PullRequest(
        number = 1,
        secretKey = "key",
        creatorName = "testns",
        repoId = 1,
        repoFullName = repoName,
        headSha = "6e8f7b3a763ff13e83dd3d7140b8794d52886943",
        branchName = branchName,
        gitService = GitProperty.GITHUB
    )

    private val solutionStorage = mock<SolutionStorage>()

    private val fileNamesToFileTexts = mapOf("class1.java" to "class1 content", "class2.java" to "class2 content")

    private val repository = mock<Repository> {
        on { it.filePatterns } doReturn fileNamesToFileTexts.keys
    }

    private val repositoryRepository = mock<RepositoryRepository> {
        on { it.findByName(pullRequest.repoFullName) } doReturn repository
    }
    private val authorizationService = mock<AuthorizationService> {
        on { it.getAuthorizationToken(pullRequest.secretKey) } doReturn "b87dc2265e55c1686d1a79ef83a417b49f96b34c"
    }

    private val githubLoader = GithubLoader(solutionStorage, repositoryRepository, authorizationService)

    @Test
    @Ignore
    fun loadFileText() {
        githubLoader.loadFilesFromGit(pullRequest)

        fileNamesToFileTexts.entries.forEach {
            verify(solutionStorage).saveSolution(pullRequest, it.key, it.value)
        }

    }
}