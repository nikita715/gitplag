package ru.nikstep.redink.analysis.solutions

import mu.KotlinLogging
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.PreparedAnalysisData
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.SourceCode
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.util.AnalysisBranchMode.*
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
    override fun loadAllBasesAndSolutions(analysisSettings: AnalysisSettings) =
        PreparedAnalysisData(
            repoName = analysisSettings.repository.name,
            language = analysisSettings.language,
            analyser = analysisSettings.analyser,
            bases = loadBases(analysisSettings),
            solutions = loadSolutions(analysisSettings)
        )

    override fun loadBases(analysisSettings: AnalysisSettings): List<File> =
        Files.walk(pathToBases(analysisSettings).asPath())
            .filter { path -> Files.isRegularFile(path) }
            .map(Path::toFile).collect(toList())

    private fun loadSolutions(analysisSettings: AnalysisSettings): List<File> =
        loadSourceCodeForAnalysis(analysisSettings).map {
            val file = pathToSolution(analysisSettings, it).asFile()
            if (file.exists())
                file
            else
                throw SolutionNotFoundException(
                    "Loader: solution ${it.repo}/${it.sourceBranch}/${it.fileName} not found"
                )
        }

    private fun loadSourceCodeForAnalysis(analysisSettings: AnalysisSettings) =
        when (analysisSettings.branchMode) {
            BY_TARGET -> sourceCodeRepository
                .findAllByRepoAndTargetBranch(analysisSettings.repository.name, analysisSettings.branch)
            BY_SOURCE -> sourceCodeRepository
                .findAllByRepoAndSourceBranch(analysisSettings.repository.name, analysisSettings.branch)
            ANY -> sourceCodeRepository
                .findAllByRepo(analysisSettings.repository.name)
        }

    @Synchronized
    override fun saveSolution(pullRequest: PullRequest, fileName: String, fileText: String): File {
        val pathToSolution = pathToSolution(pullRequest, fileName)
        sourceCodeRepository.deleteByRepoAndUserAndFileNameAndSourceBranch(
            pullRequest.mainRepoFullName,
            pullRequest.creatorName,
            fileName,
            pullRequest.sourceBranchName
        )
        sourceCodeRepository.save(SourceCode(pullRequest, fileName))
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

    private fun pathToSolution(git: GitProperty, repo: String, branch: String, creator: String, file: String): String =
        asPath(solutionsDir, git, repo, branch, creator, file)

    private fun pathToBases(analysisSettings: AnalysisSettings): String =
        pathToBases(analysisSettings.gitService, analysisSettings.repository.name, analysisSettings.branch)

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
}