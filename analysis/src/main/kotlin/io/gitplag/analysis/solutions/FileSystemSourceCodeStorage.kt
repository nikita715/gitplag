package io.gitplag.analysis.solutions

import io.gitplag.analysis.analysisFilesDirectoryName
import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.data.PreparedAnalysisData
import io.gitplag.model.data.Solution
import io.gitplag.model.entity.*
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.BaseFileRecordRepository
import io.gitplag.model.repo.PullRequestRepository
import io.gitplag.model.repo.SolutionFileRecordRepository
import io.gitplag.util.asPath
import io.gitplag.util.forEachFileInDirectory
import mu.KotlinLogging
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime

/**
 * Storage of source files based on file system
 */
class FileSystemSourceCodeStorage(
    private val baseFileRecordRepository: BaseFileRecordRepository,
    private val repositoryDataManager: RepositoryDataManager,
    private val solutionFileRecordRepository: SolutionFileRecordRepository,
    private val pullRequestRepository: PullRequestRepository,
    private val solutionsDir: String,
    private val jplagResultDir: String,
    private val analysisFilesDir: String
) : SourceCodeStorage {

    private val logger = KotlinLogging.logger {}
    private val baseDir = ".base"

    override fun loadBasesAndComposedSolutions(settings: AnalysisSettings, tempDir: String) =
        loadBasesAndSolutions(settings, tempDir, loadComposedSolutions(settings, tempDir))

    override fun loadBasesAndSeparatedSolutions(settings: AnalysisSettings, tempDir: String) =
        loadBasesAndSolutions(settings, tempDir, loadSeparateSolutions(settings, tempDir))

    private fun loadBases(settings: AnalysisSettings, tempDir: String): List<File> {
        val filePatterns = repositoryDataManager.findFileNameRegexps(settings.repository)
        val baseDir = File("$tempDir/.base")
        Files.createDirectory(baseDir.toPath())
        var fileIterator = 0
        return baseFileRecordRepository.findAllByRepo(settings.repository)
            .mapNotNull { baseRecord ->
                if (nameMatchesRegex(baseRecord.fileName, filePatterns)) {
                    val generatedFileName = "${fileIterator++}.${baseRecord.fileName.toFileExtension()}"
                    val generatedFile = File(baseDir.absolutePath + "/" + generatedFileName)
                    Files.copy(
                        File(
                            pathToBase(
                                settings.repository.gitService, settings.repository.name,
                                baseRecord.branch, baseRecord.fileName
                            )
                        ).toPath(),
                        generatedFile.toPath()
                    )
                    generatedFile
                } else null
            }
    }

    private fun loadBasesAndSolutions(
        settings: AnalysisSettings,
        rootDir: String,
        solutions: List<Solution>
    ) =
        PreparedAnalysisData(
            gitService = settings.repository.gitService,
            repoName = settings.repository.name,
            language = settings.language,
            bases = loadBases(settings, rootDir),
            solutions = solutions,
            rootDir = rootDir,
            resultSize = settings.maxResultSize,
            minPercentage = settings.minResultPercentage
        )

    private fun loadSeparateSolutions(
        analysisSettings: AnalysisSettings,
        tempDir: String
    ): List<Solution> {
        val filePatterns = repositoryDataManager.findFileNameRegexps(analysisSettings.repository)
        return pullRequestRepository.findAllByRepoAndSourceBranchName(
            analysisSettings.repository,
            analysisSettings.branch
        ).flatMap { pullRequest ->
            solutionFileRecordRepository.findAllByPullRequest(pullRequest)
                .filter { solutionRecord -> nameMatchesRegex(solutionRecord.fileName, filePatterns) }
                .map { solutionRecord ->
                    val file = File(pathToSolution(analysisSettings.repository, solutionRecord))
                    val copiedFile = File("$tempDir/${pullRequest.creatorName}/${solutionRecord.fileName}")
                    copiedFile.parentFile.mkdirs()
                    Files.copy(file.toPath(), copiedFile.toPath())
                    Solution(
                        student = pullRequest.creatorName,
                        fileName = solutionRecord.fileName,
                        file = copiedFile,
                        sha = pullRequest.headSha,
                        createdAt = pullRequest.createdAt
                    )
                }
        }
    }

    private fun loadComposedSolutions(
        analysisSettings: AnalysisSettings,
        tempDir: String
    ): List<Solution> {
        val filePatterns = repositoryDataManager.findFileNameRegexps(analysisSettings.repository)
        return repositoryDataManager.findById(analysisSettings.additionalRepositories).plus(analysisSettings.repository)
            .flatMap { repository ->
                pullRequestRepository.findAllByRepoAndSourceBranchName(
                    repository,
                    analysisSettings.branch
                ).map { pullRequest ->
                    val solutionRecords = solutionFileRecordRepository.findAllByPullRequest(pullRequest)
                        .filter { solutionRecord -> nameMatchesRegex(solutionRecord.fileName, filePatterns) }
                    val extension = solutionRecords.getOrNull(0)?.fileName?.toFileExtension()
                    val fileName = pullRequest.creatorName + "." + (extension ?: "txt")
                    val composedFile = File("$tempDir/${pullRequest.creatorName}/$fileName")
                    composedFile.parentFile.mkdir()
                    solutionRecords.forEachIndexed { index, solutionRecord ->
                        val solFile = File(pathToSolution(repository, solutionRecord))
                        composedFile.appendText(solFile.readText())
                        if (index != solutionRecords.size - 1) {
                            composedFile.appendText("\n")
                        }
                    }
                    Solution(
                        student = pullRequest.creatorName,
                        fileName = fileName,
                        file = composedFile,
                        sha = pullRequest.headSha,
                        createdAt = pullRequest.createdAt
                    )
                }
            }
    }

    override fun saveBasesFromDir(
        tempDir: String,
        repo: Repository,
        branchName: String
    ) {
        val pathToBases = pathToBases(repo.gitService, repo.name, branchName)
        File(pathToBases).deleteRecursively()
        baseFileRecordRepository.deleteAllByRepoAndBranch(repo, branchName)
        val filePatterns = repositoryDataManager.findFileNameRegexps(repo)
        forEachFileInDirectory(tempDir) { foundedFile ->
            val fileName = File(tempDir).toPath().relativize(foundedFile.toPath()).toString().replace("\\", "/")
            if (nameMatchesRegex(fileName, filePatterns)) {
                foundedFile.copyTo(File("$pathToBases/$fileName"))
                baseFileRecordRepository.save(
                    BaseFileRecord(
                        repo = repo,
                        fileName = fileName,
                        branch = branchName
                    )
                )
                logger.info { "Storage: Saved base file with name = $fileName of repo ${repo.name}" }
            }
        }
    }

    override fun saveSolutionsFromDir(
        tempDir: String,
        pullRequest: PullRequest
    ) {
        val pathToSolutions = pathToSolutions(pullRequest)
        File(pathToSolutions).deleteRecursively()
        solutionFileRecordRepository.deleteAllByPullRequest(pullRequest)
        val filePatterns = repositoryDataManager.findFileNameRegexps(pullRequest.repo)
        forEachFileInDirectory(tempDir) { foundedFile ->
            val fileName = File(tempDir).toPath().relativize(foundedFile.toPath()).toString().replace("\\", "/")
            if (nameMatchesRegex(fileName, filePatterns)) {
                foundedFile.copyTo(File("$pathToSolutions/$fileName"))
                solutionFileRecordRepository.save(
                    SolutionFileRecord(
                        pullRequest = pullRequest,
                        fileName = fileName
                    )
                )
                logger.info {
                    "Storage: Saved solution file with name = $fileName of" +
                            " repo ${pullRequest.repo.name}, user ${pullRequest.creatorName}"
                }
            }
        }
    }

    override fun getAnalysisFiles(analysis: Analysis, user: String): List<File> {
        val directoryName = analysisFilesDirectoryName(analysis)
        return listOf(File("$analysisFilesDir/$directoryName/$user").listFiles()[0])
    }

    override fun deleteAnalysisFiles(analysis: Analysis) {
        val directoryName = analysisFilesDirectoryName(analysis)
        File("$analysisFilesDir/$directoryName").deleteRecursively()
        if (analysis.analyzer == AnalyzerProperty.JPLAG) {
            File("$jplagResultDir/$directoryName").deleteRecursively()
        }
    }

    override fun deleteAnalysisFiles(repoName: String, executionDate: LocalDateTime, analyzer: AnalyzerProperty) {
        val directoryName = analysisFilesDirectoryName(repoName, executionDate)
        File("$analysisFilesDir/$directoryName").deleteRecursively()
        if (analyzer == AnalyzerProperty.JPLAG) {
            File("$jplagResultDir/$directoryName").deleteRecursively()
        }
    }

    override fun deleteBaseFile(repo: Repository, branch: String, fileName: String) {
        File(pathToBase(repo.gitService, repo.name, branch, fileName)).deleteRecursively()
    }

    override fun deleteSolutionFile(repo: Repository, branch: String, creator: String, fileName: String) {
        File(pathToSolution(repo.gitService, repo.name, branch, creator, fileName)).deleteRecursively()
    }

    override fun deleteAllBaseFiles(repo: Repository, branch: String) {
        File(pathToBases(repo.gitService, repo.name, branch)).deleteRecursively()
    }

    override fun deleteAllSolutionFiles(repo: Repository, branch: String, creator: String) {
        File(pathToSolutions(repo.gitService, repo.name, branch, creator)).deleteRecursively()
    }

    private fun String.toFileExtension() =
        substringAfterLast(".")

    private fun nameMatchesRegex(fileName: String, filePatterns: Collection<String>): Boolean {
        if (filePatterns.isEmpty()) return true
        filePatterns.forEach {
            if (it.toRegex().matches(fileName)) return true
        }
        return false
    }

    private fun pathToBases(git: GitProperty, repo: String, branch: String): String =
        asPath(solutionsDir, git, repo, branch.toLowerCase(), baseDir)

    private fun pathToBase(git: GitProperty, repo: String, branch: String, file: String): String =
        asPath(solutionsDir, git, repo, branch.toLowerCase(), baseDir, file)

    private fun pathToSolution(git: GitProperty, repo: String, branch: String, creator: String, file: String): String =
        asPath(solutionsDir, git, repo, branch.toLowerCase(), creator, file)

    private fun pathToSolutions(git: GitProperty, repo: String, branch: String, creator: String): String =
        asPath(solutionsDir, git, repo, branch.toLowerCase(), creator)

    private fun pathToSolutions(pullRequest: PullRequest): String =
        pathToSolutions(
            pullRequest.repo.gitService, pullRequest.repo.name,
            pullRequest.sourceBranchName.toLowerCase(), pullRequest.creatorName
        )

    private fun pathToBase(analysisSettings: AnalysisSettings, fileName: String): String =
        pathToBase(
            analysisSettings.repository.gitService, analysisSettings.repository.name,
            analysisSettings.branch, fileName
        )

    private fun pathToSolution(
        repo: Repository,
        solutionFileRecord: SolutionFileRecord
    ): String =
        pathToSolution(
            repo.gitService,
            repo.name,
            solutionFileRecord.pullRequest.sourceBranchName,
            solutionFileRecord.pullRequest.creatorName,
            solutionFileRecord.fileName
        )

}