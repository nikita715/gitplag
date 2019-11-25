package io.gitplag.core.graphql

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.gitplag.git.agent.GitAgent
import io.gitplag.git.payload.PayloadProcessor
import io.gitplag.model.entity.BaseFileRecord
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.entity.SolutionFileRecord
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.BaseFileRecordRepository
import io.gitplag.model.repo.SolutionFileRecordRepository
import java.time.LocalDateTime

class SourceFileQueriesTest {

    private val repoName = "repoName"
    private val github = GitProperty.GITHUB
    private val repo = Repository(gitService = github, name = repoName, gitId = "")
    private val branch1 = "branch"
    private val branch2 = "branch2"
    private val fileName1 = "file1"
    private val fileName2 = "file2"
    private val student1 = "user1"
    private val student2 = "user2"

    private val pullRequest1 = PullRequest(
        repo = repo, number = 1, creatorName = student1,
        sourceBranchName = branch1, mainBranchName = branch1, headSha = "sha1",
        sourceRepoFullName = "sourceRepo", createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now()
    )

    private val pullRequest2 = PullRequest(
        repo = repo, number = 1, creatorName = student2,
        sourceBranchName = branch2, mainBranchName = branch2, headSha = "sha2",
        sourceRepoFullName = "sourceRepo", createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now()
    )

    private val base1 = BaseFileRecord(repo = repo, fileName = fileName1, branch = branch1)
    private val base2 = BaseFileRecord(repo = repo, fileName = fileName2, branch = branch2)
    private val sol1 = SolutionFileRecord(pullRequest = pullRequest1, fileName = fileName1)
    private val sol2 = SolutionFileRecord(pullRequest = pullRequest2, fileName = fileName2)

    private val solutionFileRecordRepository = mock<SolutionFileRecordRepository> {
        on { findAllByRepo(repo) } doReturn listOf(sol1, sol2)
    }

    private val baseFileRecordRepository = mock<BaseFileRecordRepository> {
        on { findAllByRepo(repo) } doReturn listOf(base1, base2)
    }
    private val repositoryDataManager = mock<RepositoryDataManager> {
        on { findByGitServiceAndName(github, repoName) } doReturn repo
    }
    private val restManager = mock<GitAgent>()
    private val payloadProcessor = mock<PayloadProcessor>()

    private val restManagers: Map<GitProperty, GitAgent> = mapOf(github to restManager)
    private val payloadProcessors = mapOf(github to payloadProcessor)

//    private val sourceFileQueries = SourceFileQueries(
//        solutionFileRecordRepository, baseFileRecordRepository,
//        repositoryDataManager, restManagers, payloadProcessors
//    )
//
//    @Test
//    fun getLocalBases() {
//        sourceFileQueries.getLocalBases(github, repoName, branch1, fileName1) shouldBe listOf(base1)
//    }
//
//    @Test
//    fun getLocalSolutions() {
//        sourceFileQueries.getLocalSolutions(github, repoName, branch1, student1, fileName1) shouldBe listOf(sol1)
//    }
//
//    @Test
//    fun updateFilesOfRepo() {
//        sourceFileQueries.updateFilesOfRepo(github, repoName) shouldBe
//                ComposedFiles(bases = listOf(base1, base2), solutions = listOf(sol1, sol2))
//        verify(restManager).cloneRepository(repo)
//        verify(payloadProcessor).downloadAllPullRequestsOfRepository(repo)
//    }
}