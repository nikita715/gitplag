package ru.nikstep.redink.analysis.solutions

import mu.KotlinLogging
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.PreparedAnalysisData
import ru.nikstep.redink.model.data.Solution
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

    override fun loadBase(gitProperty: GitProperty, repoName: String, branchName: String, fileName: String): File =
        pathToBase(gitProperty, repoName, branchName, fileName).asFile()

    @Synchronized
    override fun loadBasesAndSeparateSolutions(analysisSettings: AnalysisSettings) =
        PreparedAnalysisData(
            repoName = analysisSettings.repository.name,
            language = analysisSettings.language,
            bases = loadBases(analysisSettings),
            solutions = loadSeparateSolutions(analysisSettings)
        )

    @Synchronized
    override fun loadBasesAndComposedSolutions(analysisSettings: AnalysisSettings) =
        PreparedAnalysisData(
            repoName = analysisSettings.repository.name,
            language = analysisSettings.language,
            bases = loadBases(analysisSettings),
            solutions = loadComposedSolutions(analysisSettings)
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

    private fun loadComposedSolutions(analysisSettings: AnalysisSettings): List<Solution> {
        return loadSourceCodeForAnalysis(analysisSettings).groupBy { it.user }
            .map {
                val path1 = Paths.get("temp_solutions", it.key)
                val fileName = it.key + ".java"
                val path2 = Paths.get("temp_solutions", it.key, fileName)
                val path3 = pathToSolutions(analysisSettings, it.key).asPath()
                Files.deleteIfExists(path2)
                Files.createDirectories(path1)
                val solFile = Files.createFile(path2).toFile()
                var allLength = 0
                val files = mutableListOf<String>()
                val fileLengths = mutableListOf<Int>()
                Files.walk(pathToSolutions(analysisSettings, it.key).asPath())
                    .filter { path -> Files.isRegularFile(path) && !Files.isHidden(path) }
                    .map(Path::toFile).collect(toList()).forEach { file ->
                        //                        solFile.appendText("//" +  + System.lineSeparator())
                        solFile.appendText(file.readText())
                        val length = Files.lines(file.toPath()).count().toInt()
                        files += path3.relativize(file.toPath()).toString()
                        fileLengths += length + allLength
                        allLength += length
                    }
                Solution(it.key, fileName, solFile, files, fileLengths, it.value[0].sha)
            }
    }

    private fun loadSourceCodeForAnalysis(analysisSettings: AnalysisSettings) =
        sourceCodeRepository
            .findAllByRepoAndSourceBranch(analysisSettings.repository.name, analysisSettings.branch)

    @Synchronized
    override fun saveSolution(pullRequest: PullRequest, fileName: String, fileText: String): File {
        val pathToSolution = pathToSolution(pullRequest, fileName)
        sourceCodeRepository.deleteByRepoAndUserAndFileNameAndSourceBranch(
            pullRequest.mainRepoFullName,
            pullRequest.creatorName,
            fileName,
            pullRequest.sourceBranchName
        )
        sourceCodeRepository.save(SourceCode(pullRequest, fileName, countOfLines(fileText)))
        return saveLocally(pathToSolution, fileText)
    }

    @Synchronized
    override fun saveBase(pullRequest: PullRequest, fileName: String, fileText: String): File {
        val pathToBase =
            pathToBase(pullRequest.gitService, pullRequest.mainRepoFullName, pullRequest.sourceBranchName, fileName)
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

    private fun pathToSolutions(git: GitProperty, repo: String, branch: String): String =
        asPath(solutionsDir, git, repo, branch)

    private fun pathToSolutions(git: GitProperty, repo: String, branch: String, user: String): String =
        asPath(solutionsDir, git, repo, branch, user)

    private fun pathToSolution(git: GitProperty, repo: String, branch: String, creator: String, file: String): String =
        asPath(solutionsDir, git, repo, branch, creator, file)

    private fun pathToBases(analysisSettings: AnalysisSettings): String =
        pathToBases(analysisSettings.gitService, analysisSettings.repository.name, analysisSettings.branch)

    private fun pathToSolutions(analysisSettings: AnalysisSettings, user: String): String =
        pathToSolutions(analysisSettings.gitService, analysisSettings.repository.name, analysisSettings.branch, user)

    private fun pathToSolutions(analysisSettings: AnalysisSettings): String =
        pathToSolutions(analysisSettings.gitService, analysisSettings.repository.name, analysisSettings.branch)

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

    private fun String.asPath() = Paths.get(this)

    private fun String.asFile() = File(this.asPath().toUri())

    private fun File.subfiles() = this.list().toList()

    private fun countOfLines(text: String) = "\r\n|\r|\n".toRegex().findAll(text).count()

}