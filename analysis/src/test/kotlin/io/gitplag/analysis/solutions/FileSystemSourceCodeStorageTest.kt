package io.gitplag.analysis.solutions

import com.nhaarman.mockitokotlin2.*
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.entity.BaseFileRecord
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.entity.SolutionFileRecord
import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.BaseFileRecordRepository
import io.gitplag.model.repo.PullRequestRepository
import io.gitplag.model.repo.SolutionFileRecordRepository
import io.gitplag.util.asPath
import io.gitplag.util.inTempDirectory
import io.kotlintest.matchers.shouldBe
import org.apache.commons.io.FileUtils
import org.junit.Test
import java.io.File
import java.time.LocalDateTime.now
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Tests for the [FileSystemSourceCodeStorage]
 */
class FileSystemSourceCodeStorageTest {

    private val resourcesPath = asPath("src", "test", "resources")
    private val unpackedZip = File(asPath(resourcesPath, "unpackedzip")).absolutePath
    private val solutionDir = File(asPath(resourcesPath, "solutiondirsample")).absolutePath
    private val composedFileDir = File(asPath(resourcesPath, "composedfiles")).absolutePath

    private val analysisFilesDir = "analysisFilesDir"
    private val jplagResultDir = "jplagResultDir"

    private val github = GitProperty.GITHUB
    private val java = Language.JAVA

    private val fileName1 = "File1.java"
    private val fileName2 = "dir/File2.java"
    private val fileName3 = "File3.txt"
    private val fileName4 = "dir/File4.txt"

    private val repoName = "repo"
    private val branchName = "br"
    private val student = "stud"
    private val student2 = "stud2"

    private val repo = mock<Repository> {
        on { id } doReturn 1
        on { gitService } doReturn github
        on { name } doReturn repoName
        on { language } doReturn java
        on { analyzer } doReturn AnalyzerProperty.MOSS
        on { analysisMode } doReturn AnalysisMode.LINK
    }

    private val sha1 = "sha1"
    private val sha2 = "sha2"

    private val pullRequest = mock<PullRequest> {
        on { repo } doReturn repo
        on { sourceBranchName } doReturn branchName
        on { creatorName } doReturn student
        on { headSha } doReturn sha1
        on { createdAt } doReturn now()
    }

    private val pullRequest2 = mock<PullRequest> {
        on { repo } doReturn repo
        on { sourceBranchName } doReturn branchName
        on { creatorName } doReturn student2
        on { headSha } doReturn sha2
        on { createdAt } doReturn now().minusSeconds(1)
    }

    private val pathToFiles = "$solutionDir/$github/$repoName/$branchName"

    private val baseFile1 = File("$pathToFiles/.base/$fileName1")
    private val baseFile2 = File("$pathToFiles/.base/$fileName2")
    private val baseFile3 = File("$pathToFiles/.base/$fileName3")
    private val baseFile4 = File("$pathToFiles/.base/$fileName4")
    private val solFile1 = File("$pathToFiles/$student/$fileName1")
    private val solFile2 = File("$pathToFiles/$student/$fileName2")
    private val solFile3 = File("$pathToFiles/$student/$fileName3")
    private val solFile4 = File("$pathToFiles/$student/$fileName4")
    private val solFile5 = File("$pathToFiles/$student2/$fileName1")
    private val solFile6 = File("$pathToFiles/$student2/$fileName2")
    private val solFile7 = File("$pathToFiles/$student2/$fileName3")
    private val solFile8 = File("$pathToFiles/$student2/$fileName4")

    private val baseFileRecord1 = BaseFileRecord(-1, repo, fileName1, branchName)
    private val baseFileRecord2 = BaseFileRecord(-1, repo, fileName2, branchName)
    private val baseFileRecord3 = BaseFileRecord(-1, repo, fileName3, branchName)
    private val baseFileRecord4 = BaseFileRecord(-1, repo, fileName4, branchName)

    private val solFileRecord1 = SolutionFileRecord(-1, pullRequest, fileName1)
    private val solFileRecord2 = SolutionFileRecord(-1, pullRequest, fileName2)
    private val solFileRecord3 = SolutionFileRecord(-1, pullRequest, fileName3)
    private val solFileRecord4 = SolutionFileRecord(-1, pullRequest, fileName4)
    private val solFileRecord5 = SolutionFileRecord(-1, pullRequest2, fileName1)
    private val solFileRecord6 = SolutionFileRecord(-1, pullRequest2, fileName2)
    private val solFileRecord7 = SolutionFileRecord(-1, pullRequest2, fileName3)
    private val solFileRecord8 = SolutionFileRecord(-1, pullRequest2, fileName4)

    private lateinit var sourceCodeStorage: FileSystemSourceCodeStorage

    private val repositoryDataManager = mock<RepositoryDataManager> {
        on { findFileNameRegexps(repo) } doReturn listOf(".+\\.java")
    }

    private val pullRequestRepository = mock<PullRequestRepository> {
        on { findAllByRepoIdInAndSourceBranchName(listOf(1), branchName) } doReturn listOf(pullRequest, pullRequest2)
    }

    private val baseFileRecordRepository = mock<BaseFileRecordRepository> {
        on { findAllByRepo(repo) } doReturn
                listOf(baseFileRecord1, baseFileRecord2, baseFileRecord3, baseFileRecord4)
    }

    private val solutionFileRecordRepository = mock<SolutionFileRecordRepository> {
        on { findAllByPullRequest(pullRequest) } doReturn
                listOf(solFileRecord1, solFileRecord2, solFileRecord3, solFileRecord4)
        on { findAllByPullRequest(pullRequest2) } doReturn
                listOf(solFileRecord5, solFileRecord6, solFileRecord7, solFileRecord8)
    }

    private val studJava = "stud.java"
    private val stud2Java = "stud2.java"
    private val base0Java = "0.java"
    private val base1Java = "1.java"

    private val composedSolution1 = File("$composedFileDir/$studJava")
    private val composedSolution2 = File("$composedFileDir/$stud2Java")

    @Test
    fun loadBasesAndComposedSolutions() {
        sourceCodeStorage = FileSystemSourceCodeStorage(
            baseFileRecordRepository, repositoryDataManager,
            solutionFileRecordRepository, pullRequestRepository, solutionDir, jplagResultDir, analysisFilesDir
        )

        val analysisSettings = AnalysisSettings(repo, branchName, listOf(".+\\.java"))

        inTempDirectory { tempDir ->
            val analysisData =
                sourceCodeStorage.loadBasesAndSolutions(analysisSettings, tempDir)

            analysisData.bases.size shouldBe 2

            analysisData.bases[0].name shouldBe base0Java
            analysisData.bases[1].name shouldBe base1Java

            FileUtils.contentEquals(analysisData.bases[0], baseFile1) shouldBe true
            FileUtils.contentEquals(analysisData.bases[1], baseFile2) shouldBe true

            analysisData.solutions.size shouldBe 2
            analysisData.gitService shouldBe github
            analysisData.language shouldBe java
            analysisData.repoName shouldBe repoName

            val sortedSolutions = analysisData.solutions.sortedBy { it.fileName }
            val solution1 = sortedSolutions[0]
            val solution2 = sortedSolutions[1]

            solution1.fileName shouldBe studJava
            solution1.sha shouldBe sha1
            solution1.student shouldBe student
            FileUtils.contentEquals(solution1.file, composedSolution1) shouldBe true

            solution2.fileName shouldBe stud2Java
            solution2.sha shouldBe sha2
            solution2.student shouldBe student2
            FileUtils.contentEquals(solution2.file, composedSolution2) shouldBe true
        }
    }

    @Test
    fun saveBasesFromDir() {
        inTempDirectory { tempDir ->
            sourceCodeStorage = FileSystemSourceCodeStorage(
                baseFileRecordRepository, repositoryDataManager,
                solutionFileRecordRepository, pullRequestRepository, tempDir, jplagResultDir, analysisFilesDir
            )

            sourceCodeStorage.saveBasesFromDir(unpackedZip, repo, branchName)

            verify(baseFileRecordRepository).deleteAllByRepoAndBranch(repo, branchName)

            verify(baseFileRecordRepository).save(eq(baseFileRecord1))
            verify(baseFileRecordRepository).save(eq(baseFileRecord2))
            verify(baseFileRecordRepository, never()).save(eq(baseFileRecord3))
            verify(baseFileRecordRepository, never()).save(eq(baseFileRecord4))

            assertTrue { File("$tempDir/$github/$repoName/$branchName/.base/$fileName1").exists() }
            assertTrue { File("$tempDir/$github/$repoName/$branchName/.base/$fileName2").exists() }
            assertFalse { File("$tempDir/$github/$repoName/$branchName/.base/$fileName3").exists() }
            assertFalse { File("$tempDir/$github/$repoName/$branchName/.base/$fileName4").exists() }
        }
    }

    @Test
    fun saveSolutionsFromDir() {
        inTempDirectory { tempDir ->
            sourceCodeStorage = FileSystemSourceCodeStorage(
                baseFileRecordRepository, repositoryDataManager,
                solutionFileRecordRepository, pullRequestRepository, tempDir, jplagResultDir, analysisFilesDir
            )

            sourceCodeStorage.saveSolutionsFromDir(unpackedZip, pullRequest)

            verify(solutionFileRecordRepository).deleteAllByPullRequest(pullRequest)

            verify(solutionFileRecordRepository).save(eq(solFileRecord1))
            verify(solutionFileRecordRepository).save(eq(solFileRecord2))
            verify(solutionFileRecordRepository, never()).save(eq(solFileRecord3))
            verify(solutionFileRecordRepository, never()).save(eq(solFileRecord4))

            assertTrue { File("$tempDir/$github/$repoName/$branchName/$student/$fileName1").exists() }
            assertTrue { File("$tempDir/$github/$repoName/$branchName/$student/$fileName2").exists() }
            assertFalse { File("$tempDir/$github/$repoName/$branchName/$student/$fileName3").exists() }
            assertFalse { File("$tempDir/$github/$repoName/$branchName/$student/$fileName4").exists() }
        }
    }
}