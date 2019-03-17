package ru.nikstep.redink.analysis.solutions

import mu.KotlinLogging
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.PreparedAnalysisData
import ru.nikstep.redink.model.data.Solution
import ru.nikstep.redink.model.data.SourceFileInfo
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.SourceCode
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.asPath
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors.toList

class FileSystemSolutionStorage(
    private val sourceCodeRepository: SourceCodeRepository,
    private val solutionsDir: String
) : SolutionStorage {

    private val logger = KotlinLogging.logger {}
    private val baseDir = ".base"

    override fun saveBases(
        tempDir: String,
        gitService: GitProperty,
        repoFullName: String,
        branchName: String
    ) {
        val pathToBases = pathToBases(gitService, repoFullName, branchName)
        Files.deleteIfExists(pathToBases.asPath())
        tempDir.asFile().listFiles().forEach { file ->
            file.copyRecursively(
                File("$pathToBases/${file.name}")
            )
        }
    }

    override fun loadBase(gitProperty: GitProperty, repoName: String, branchName: String, fileName: String): File =
        pathToBase(gitProperty, repoName, branchName, fileName).asFile()

    @Synchronized
    override fun loadBasesAndComposedSolutions(analysisSettings: AnalysisSettings, tempDir: String) =
        PreparedAnalysisData(
            gitService = analysisSettings.gitService,
            repoName = analysisSettings.repository.name,
            language = analysisSettings.language,
            bases = loadBases(analysisSettings),
            solutions = loadComposedSolutions(analysisSettings, tempDir)
        )

    @Synchronized
    override fun loadBasesAndSeparatedSolutions(analysisSettings: AnalysisSettings) =
        PreparedAnalysisData(
            gitService = analysisSettings.gitService,
            repoName = analysisSettings.repository.name,
            language = analysisSettings.language,
            bases = loadBases(analysisSettings),
            solutions = loadSeparateSolutions(analysisSettings)
        )

    @Synchronized
    override fun loadBasesAndSeparatedCopiedSolutions(analysisSettings: AnalysisSettings, tempDir: String) =
        PreparedAnalysisData(
            gitService = analysisSettings.gitService,
            repoName = analysisSettings.repository.name,
            language = analysisSettings.language,
            bases = loadBases(analysisSettings),
            solutions = loadSeparateCopiedSolutions(analysisSettings, tempDir)
        )

    override fun loadBases(analysisSettings: AnalysisSettings): List<File> =
        Files.walk(pathToBases(analysisSettings).asPath())
            .filter { path -> Files.isRegularFile(path) }
            .map(Path::toFile).collect(toList())

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

    private fun loadSeparateCopiedSolutions(analysisSettings: AnalysisSettings, tempDir: String): List<Solution> {
        return loadSourceCodeForAnalysis(analysisSettings).groupBy { it.user }
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

    private fun loadSourceCodeForAnalysis(analysisSettings: AnalysisSettings) =
        sourceCodeRepository
            .findAllByRepoAndSourceBranch(analysisSettings.repository.name, analysisSettings.branch)

    @Synchronized
    override fun saveSolution(pullRequest: PullRequest, fileName: String, fileText: String): SourceCode {
        val pathToSolution = pathToSolution(pullRequest, fileName)
        sourceCodeRepository.deleteByRepoAndUserAndFileNameAndSourceBranch(
            pullRequest.mainRepoFullName,
            pullRequest.creatorName,
            fileName,
            pullRequest.sourceBranchName
        )
        val savedFile = saveLocally(pathToSolution, fileText)
        val fileLength = Files.lines(savedFile.toPath()).count().toInt()
        return sourceCodeRepository.save(SourceCode(pullRequest, fileName, fileLength))
    }

    override fun saveSolution(sourceFileInfo: SourceFileInfo): SourceCode {
        val pathToSolution = pathToSolution(sourceFileInfo)
        sourceCodeRepository.deleteByRepoAndUserAndFileNameAndSourceBranch(
            sourceFileInfo.mainRepoFullName,
            sourceFileInfo.creator,
            sourceFileInfo.fileName,
            sourceFileInfo.sourceBranchName
        )
        val savedFile = saveLocally(pathToSolution, sourceFileInfo.fileText)
        val fileLength = Files.lines(savedFile.toPath()).count().toInt()
        return sourceCodeRepository.save(SourceCode(sourceFileInfo, fileLength))
    }

    @Synchronized
    override fun saveBase(pullRequest: PullRequest, fileName: String, fileText: String): File {
        return saveBase(
            pullRequest.gitService,
            pullRequest.mainRepoFullName,
            pullRequest.sourceBranchName,
            fileName,
            fileText
        )
    }

    override fun saveBase(
        gitService: GitProperty,
        mainRepoFullName: String,
        sourceBranchName: String,
        fileName: String,
        fileText: String
    ): File {
        val pathToBase =
            pathToBase(gitService, mainRepoFullName, sourceBranchName, fileName)
        return saveLocally(pathToBase, fileText)
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

    private fun pathToBases(analysisSettings: AnalysisSettings): String =
        pathToBases(analysisSettings.gitService, analysisSettings.repository.name, analysisSettings.branch)

    private fun pathToSolution(analysisSettings: AnalysisSettings, sourceCode: SourceCode): String =
        pathToSolution(
            analysisSettings.gitService, analysisSettings.repository.name,
            sourceCode.sourceBranch, sourceCode.user, sourceCode.fileName
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