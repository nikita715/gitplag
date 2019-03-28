package ru.nikstep.redink.analysis.solutions

import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import ru.nikstep.redink.model.entity.BaseFileRecord
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.SolutionFileRecord
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.manager.RepositoryDataManager
import ru.nikstep.redink.model.repo.BaseFileRecordRepository
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.model.repo.SolutionFileRecordRepository
import ru.nikstep.redink.util.asPath
import ru.nikstep.redink.util.inTempDirectory
import java.io.File

class FileSystemSourceCodeStorageTest {

    private val baseFileRecordRepository = mock<BaseFileRecordRepository>()
    private val solutionFileRecordRepository = mock<SolutionFileRecordRepository>()
    private val pullRequestRepository = mock<PullRequestRepository>()

    private val separateSolutionsDir = asPath("src", "test", "resources", "filestorage1")

    private val fileStorage = File(separateSolutionsDir).absolutePath

    private val fileName1 = "File1.java"
    private val fileName2 = "dir/File2.java"
    private val fileName3 = "File3.txt"
    private val fileName4 = "dir/File4.txt"

    private val repoName = "repo"
    private val branchName = "br"
    private val student = "stud"

    private val repo = mock<Repository> {
        on { gitService } doReturn GitProperty.GITHUB
        on { name } doReturn repoName
    }

    private val pullRequest = mock<PullRequest> {
        on { repo } doReturn repo
        on { sourceBranchName } doReturn branchName
        on { creatorName } doReturn student
    }

    private val file1 = File("$fileStorage/$fileName1")
    private val file2 = File("$fileStorage/$fileName2")
    private val file3 = File("$fileStorage/$fileName3")
    private val file4 = File("$fileStorage/$fileName4")

    private val baseFileRecord1 = BaseFileRecord(-1, repo, fileName1, branchName)
    private val baseFileRecord2 = BaseFileRecord(-1, repo, fileName2, branchName)
    private val baseFileRecord3 = BaseFileRecord(-1, repo, fileName3, branchName)
    private val baseFileRecord4 = BaseFileRecord(-1, repo, fileName4, branchName)

    private val solFileRecord1 = SolutionFileRecord(-1, pullRequest, fileName1, 2)
    private val solFileRecord2 = SolutionFileRecord(-1, pullRequest, fileName2, 4)
    private val solFileRecord3 = SolutionFileRecord(-1, pullRequest, fileName3, 6)
    private val solFileRecord4 = SolutionFileRecord(-1, pullRequest, fileName4, 8)

    private lateinit var sourceCodeStorage: FileSystemSourceCodeStorage

    private val repositoryDataManager = mock<RepositoryDataManager> {
        on { nameMatchesRegexp(fileName1, repo) } doReturn true
        on { nameMatchesRegexp(fileName2, repo) } doReturn true
        on { nameMatchesRegexp(fileName3, repo) } doReturn false
        on { nameMatchesRegexp(fileName4, repo) } doReturn false
    }

    @Test
    fun loadBasesAndComposedSolutions() {
    }

    @Test
    fun loadBasesAndSeparatedSolutions() {
    }

    @Test
    fun loadBasesAndSeparatedCopiedSolutions() {
    }

    @Test
    fun saveBasesFromDir() {
        inTempDirectory { tempDir ->
            sourceCodeStorage = FileSystemSourceCodeStorage(
                baseFileRecordRepository, repositoryDataManager,
                solutionFileRecordRepository, pullRequestRepository, tempDir
            )

            sourceCodeStorage.saveBasesFromDir(fileStorage, repo, branchName)

            verify(baseFileRecordRepository).deleteAllByRepoAndBranch(repo, branchName)

            verify(baseFileRecordRepository).save(eq(baseFileRecord1))
            verify(baseFileRecordRepository).save(eq(baseFileRecord2))
            verify(baseFileRecordRepository, never()).save(eq(baseFileRecord3))
            verify(baseFileRecordRepository, never()).save(eq(baseFileRecord4))

            assert(File("$tempDir/${GitProperty.GITHUB}/$repoName/$branchName/.base/$fileName1").exists())
            assert(File("$tempDir/${GitProperty.GITHUB}/$repoName/$branchName/.base/$fileName2").exists())
            assert(!File("$tempDir/${GitProperty.GITHUB}/$repoName/$branchName/.base/$fileName3").exists())
            assert(!File("$tempDir/${GitProperty.GITHUB}/$repoName/$branchName/.base/$fileName4").exists())
        }
    }

    @Test
    fun saveSolutionsFromDir() {
        inTempDirectory { tempDir ->
            sourceCodeStorage = FileSystemSourceCodeStorage(
                baseFileRecordRepository, repositoryDataManager,
                solutionFileRecordRepository, pullRequestRepository, tempDir
            )

            sourceCodeStorage.saveSolutionsFromDir(fileStorage, pullRequest)

            verify(solutionFileRecordRepository).deleteAllByPullRequest(pullRequest)

            verify(solutionFileRecordRepository).save(eq(solFileRecord1))
            verify(solutionFileRecordRepository).save(eq(solFileRecord2))
            verify(solutionFileRecordRepository, never()).save(eq(solFileRecord3))
            verify(solutionFileRecordRepository, never()).save(eq(solFileRecord4))

            assert(File("$tempDir/${GitProperty.GITHUB}/$repoName/$branchName/$student/$fileName1").exists())
            assert(File("$tempDir/${GitProperty.GITHUB}/$repoName/$branchName/$student/$fileName2").exists())
            assert(!File("$tempDir/${GitProperty.GITHUB}/$repoName/$branchName/$student/$fileName3").exists())
            assert(!File("$tempDir/${GitProperty.GITHUB}/$repoName/$branchName/$student/$fileName4").exists())
        }
    }
}