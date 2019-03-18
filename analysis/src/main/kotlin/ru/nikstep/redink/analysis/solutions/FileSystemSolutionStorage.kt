package ru.nikstep.redink.analysis.solutions

import mu.KotlinLogging
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.PreparedAnalysisData
import ru.nikstep.redink.model.data.Solution
import ru.nikstep.redink.model.data.SourceFileInfo
import ru.nikstep.redink.model.entity.BaseFileRecord
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.SolutionFileRecord
import ru.nikstep.redink.model.repo.BaseFileRecordRepository
import ru.nikstep.redink.model.repo.SolutionFileRecordRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.asPath
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

class FileSystemSolutionStorage(
    private val baseFileRecordRepository: BaseFileRecordRepository,
    private val solutionFileRecordRepository: SolutionFileRecordRepository,
    private val solutionsDir: String
) : SolutionStorage {

    private val logger = KotlinLogging.logger {}
    private val baseDir = ".base"

    @Synchronized
    override fun loadBasesAndComposedSolutions(settings: AnalysisSettings, tempDir: String) =
        loadBasesAndSolutions(settings, loadComposedSolutions(settings, tempDir))

    @Synchronized
    override fun loadBasesAndSeparatedSolutions(settings: AnalysisSettings) =
        loadBasesAndSolutions(settings, loadSeparateSolutions(settings))

    @Synchronized
    override fun loadBasesAndSeparatedCopiedSolutions(settings: AnalysisSettings, tempDir: String) =
        loadBasesAndSolutions(settings, loadSeparateCopiedSolutions(settings, tempDir))

    private fun loadBasesAndSolutions(settings: AnalysisSettings, solutions: List<Solution>) =
        PreparedAnalysisData(
            gitService = settings.repository.gitService,
            repoName = settings.repository.name,
            language = settings.language,
            bases = loadBases(settings),
            solutions = solutions
        )

    private fun loadSeparateSolutions(analysisSettings: AnalysisSettings): List<Solution> =
        loadSourceCodeForAnalysis(analysisSettings).map {
            val file = pathToSolution(analysisSettings, it).asFile()
            if (file.exists())
                Solution(it.user, it.fileName, file, listOf(), listOf(), it.sha)
            else
                throw SolutionNotFoundException(
                    "Loader: solution ${it.repo}/${it.sourceBranch}/${it.fileName} not found"
                )
        }

    private fun loadSeparateCopiedSolutions(analysisSettings: AnalysisSettings, tempDir: String): List<Solution> =
        loadSourceCodeForAnalysis(analysisSettings).groupBy { it.user }
            .flatMap {
                val indexToFileName = mutableListOf<Pair<String, String>>()
                val studentDir = File("$tempDir/${it.key}")
                Files.createDirectory(studentDir.toPath())
                var fileIterator = 0
                it.value.map { solutionOfStudent ->
                    val generatedFileName = "${fileIterator++}.txt"
                    val generatedFile = File(studentDir.absolutePath + "/" + generatedFileName)
                    indexToFileName += generatedFileName to solutionOfStudent.fileName
                    Files.copy(
                        pathToSolution(analysisSettings, solutionOfStudent).asPath(),
                        generatedFile.toPath()
                    )
                    Solution(
                        it.key, generatedFileName, generatedFile, sha = solutionOfStudent.sha,
                        realFileName = solutionOfStudent.fileName
                    )
                }
            }

    private fun loadComposedSolutions(analysisSettings: AnalysisSettings, tempDir: String): List<Solution> {
        return loadSourceCodeForAnalysis(analysisSettings).groupBy { it.user }
            .map {
                val fileName = it.key + ".txt"
                val composedFile = File("$tempDir/$fileName")
                var composedFileLength = 0
                val fileNames = mutableListOf<String>()
                val filePositions = mutableListOf<Int>()
                it.value.forEach { solutionOfStudent ->
                    val solFile = pathToSolution(analysisSettings, solutionOfStudent).asFile()
                    composedFile.appendText(solFile.readText())
                    filePositions += solutionOfStudent.countOfLines + composedFileLength
                    composedFileLength += solutionOfStudent.countOfLines
                    fileNames += solutionOfStudent.fileName
                }
                Solution(it.key, fileName, composedFile, fileNames, filePositions, it.value[0].sha)
            }
    }

    override fun saveBasesFromDir(
        tempDir: String,
        repo: Repository,
        branchName: String
    ) {
        val pathToBases = pathToBases(repo.gitService, repo.name, branchName)
        val tempDirPath = File(tempDir).toPath()
        Files.deleteIfExists(pathToBases.asPath())
        tempDir.asFile().listFiles().forEach { file ->
            file.copyRecursively(
                File("$pathToBases/${file.name}")
            )
            baseFileRecordRepository.save(
                BaseFileRecord(
                    repo = repo,
                    fileName = tempDirPath.relativize(file.toPath()).toString(),
                    branch = branchName
                )
            )
        }
    }

    override fun loadBase(gitProperty: GitProperty, repoName: String, branchName: String, fileName: String): File =
        pathToBase(gitProperty, repoName, branchName, fileName).asFile()

    override fun loadBases(settings: AnalysisSettings): List<File> =
        baseFileRecordRepository.findAllByRepoAndBranch(settings.repository, settings.branch)
            .map { pathToBase(settings, it.fileName).asFile() }

    private fun loadSourceCodeForAnalysis(analysisSettings: AnalysisSettings) =
        solutionFileRecordRepository
            .findAllByRepoAndSourceBranch(analysisSettings.repository.name, analysisSettings.branch)

    @Synchronized
    override fun saveSolution(pullRequest: PullRequest, fileName: String, fileText: String): SolutionFileRecord {
        val pathToSolution = pathToSolution(pullRequest, fileName)
        solutionFileRecordRepository.deleteByRepoAndUserAndFileNameAndSourceBranch(
            pullRequest.mainRepoFullName,
            pullRequest.creatorName,
            fileName,
            pullRequest.sourceBranchName
        )
        val savedFile = saveLocally(pathToSolution, fileText)
        val fileLength = Files.lines(savedFile.toPath()).count().toInt()
        return solutionFileRecordRepository.save(SolutionFileRecord(pullRequest, fileName, fileLength))
    }

    override fun saveSolution(sourceFileInfo: SourceFileInfo): SolutionFileRecord {
        val pathToSolution = pathToSolution(sourceFileInfo)
        solutionFileRecordRepository.deleteByRepoAndUserAndFileNameAndSourceBranch(
            sourceFileInfo.mainRepoFullName,
            sourceFileInfo.creator,
            sourceFileInfo.fileName,
            sourceFileInfo.sourceBranchName
        )
        val savedFile = saveLocally(pathToSolution, sourceFileInfo.fileText)
        val fileLength = Files.lines(savedFile.toPath()).count().toInt()
        return solutionFileRecordRepository.save(SolutionFileRecord(sourceFileInfo, fileLength))
    }

    override fun saveBaseByText(repo: Repository, branch: String, fileName: String, fileText: String) {
        val pathToBase =
            pathToBase(repo.gitService, repo.name, branch, fileName)
        saveLocally(pathToBase, fileText)
        baseFileRecordRepository.save(
            BaseFileRecord(
                repo = repo,
                fileName = fileName,
                branch = branch
            )
        )
    }

    private fun saveLocally(pathToFile: String, fileText: String): File {
        Files.createDirectories(pathToFile.substringBeforeLast("/").asPath())
        Files.deleteIfExists(pathToFile.asPath())
        val file = Files.createFile(pathToFile.asPath()).toFile()
        FileOutputStream(file).use { fileOutputStream -> fileOutputStream.write(fileText.toByteArray()) }
        logger.info { "File storage: saved file $pathToFile" }
        return file
    }

    private fun pathToBases(git: GitProperty, repo: String, branch: String): String =
        asPath(solutionsDir, git, repo, branch, baseDir)

    private fun pathToBase(git: GitProperty, repo: String, branch: String, file: String): String =
        asPath(solutionsDir, git, repo, branch, baseDir, file)

    private fun pathToSolution(git: GitProperty, repo: String, branch: String, creator: String, file: String): String =
        asPath(solutionsDir, git, repo, branch, creator, file)

    private fun pathToBase(analysisSettings: AnalysisSettings, fileName: String): String =
        pathToBase(
            analysisSettings.repository.gitService, analysisSettings.repository.name,
            analysisSettings.branch, fileName
        )

    private fun pathToSolution(analysisSettings: AnalysisSettings, solutionFileRecord: SolutionFileRecord): String =
        pathToSolution(
            analysisSettings.repository.gitService, analysisSettings.repository.name,
            solutionFileRecord.sourceBranch, solutionFileRecord.user, solutionFileRecord.fileName
        )

    private fun pathToSolution(pullRequest: PullRequest, fileName: String): String =
        pathToSolution(
            pullRequest.gitService,
            pullRequest.mainRepoFullName,
            pullRequest.sourceBranchName,
            pullRequest.creatorName,
            fileName
        )

    private fun pathToSolution(sourceFileInfo: SourceFileInfo): String =
        pathToSolution(
            sourceFileInfo.gitService,
            sourceFileInfo.mainRepoFullName,
            sourceFileInfo.sourceBranchName,
            sourceFileInfo.creator,
            sourceFileInfo.fileName
        )

    private fun String.asPath() = Paths.get(this)

    private fun String.asFile() = File(this.asPath().toUri())

}