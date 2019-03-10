package ru.nikstep.redink.analysis.solutions

import mu.KotlinLogging
import ru.nikstep.redink.analysis.AnalysisSettings
import ru.nikstep.redink.analysis.CommittedFile
import ru.nikstep.redink.analysis.PreparedAnalysisFiles
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.SourceCode
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.util.asPath
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors.toList

class FileSystemSolutionStorage(
    private val sourceCodeRepository: SourceCodeRepository,
    private val repositoryRepository: RepositoryRepository
) : SolutionStorage {

    override fun loadBase(repoName: String, fileName: String): File {
        return File(Paths.get(solutionsDir, repoName, baseDir, fileName).toUri())
    }

    private val logger = KotlinLogging.logger {}
    private val solutionsDir = "solutions"
    private val baseDir = ".base"

    @Synchronized
    override fun loadAllBasesAndSolutions(analysisSettings: AnalysisSettings) = PreparedAnalysisFiles(
        repoName = analysisSettings.repository.name,
        language = analysisSettings.language,
        analyser = analysisSettings.analyser,
        bases = loadBases(analysisSettings.repository.name),
        solutions = loadSolutionFiles(analysisSettings.repository.name)
    )

    override fun loadBases(repoName: String): List<File> {
        return Files.walk(Paths.get(solutionsDir, repoName, baseDir)).filter { path -> Files.isRegularFile(path) }
            .map(Path::toFile).collect(toList())
    }

    private fun loadSolutionFiles(repoName: String): Map<String, CommittedFile> {
        val solutionDirectory = File(Paths.get(solutionsDir, repoName).toUri())
        val existingDirectories = solutionDirectory.list().toList()
        return sourceCodeRepository.findAllByRepo(repoName).filter {
            existingDirectories.contains(it.user)
        }.mapNotNull {
            val file = File(Paths.get(solutionsDir, repoName, it.user, it.fileName).toUri())
            if (file.exists()) it.user to CommittedFile(file, it.sha, it.fileName) else null
        }.toMap()
    }

    override fun getCountOfSolutionFiles(repoName: String, fileName: String): Int {
        val solutionDirectory = File(Paths.get(solutionsDir, repoName).toUri())
        val directories = solutionDirectory.list().toList().intersect<String>(
            sourceCodeRepository.findAllByRepoAndFileName(repoName, fileName).map { it.user }
        )
        return directories.count { Files.exists(Paths.get(solutionsDir, repoName, it, fileName)) }
    }

    @Synchronized
    override fun saveSolution(pullRequest: PullRequest, fileName: String, fileText: String): File {
        val pathToFile = getPathToFile(pullRequest.repoFullName, fileName, pullRequest.creatorName)
        sourceCodeRepository.deleteByRepoAndUserAndFileName(pullRequest.repoFullName, pullRequest.creatorName, fileName)
        sourceCodeRepository.save(
            SourceCode(
                user = pullRequest.creatorName,
                repo = pullRequest.repoFullName,
                fileName = fileName,
                sha = pullRequest.headSha
            )
        )
        return save(pathToFile, fileText)
    }

    override fun saveBase(pullRequest: PullRequest, fileName: String, fileText: String): File {
        return saveBase(pullRequest.repoFullName, fileName, fileText)
    }

    @Synchronized
    fun saveBase(repoName: String, fileName: String, fileText: String): File {
        val pathToFile = getPathToFile(repoName, fileName, isBase = true)
        return save(pathToFile, fileText)
    }

    private fun save(pathToFile: Pair<String, String>, fileText: String): File {
        val tempDirectory = Files.createDirectories(Paths.get(pathToFile.first))
        val path = Paths.get(tempDirectory.toString(), pathToFile.second)
        Files.deleteIfExists(path)
        val file = Files.createFile(path).toFile()
        FileOutputStream(file).use { fileOutputStream -> fileOutputStream.write(fileText.toByteArray()) }
        logger.info { "File storage: saved file ${pathToFile.first}/${pathToFile.second}" }
        return file
    }

    @Synchronized
    override fun loadSolution(repoName: String, userName: String, fileName: String): File {
        val pathToFile = Paths.get(solutionsDir, repoName, userName, fileName)
        logger.info { "File storage: loaded file $repoName/$userName/$fileName" }
        return File(pathToFile.toUri())
    }

    private fun getPathToFile(
        repoFullName: String,
        repoFilePath: String,
        creator: String = "",
        isBase: Boolean = false
    ): Pair<String, String> {
        val pathElements: List<String> = repoFilePath.split("/")
        val pathBeforeFileName: String = pathElements.dropLast(1).joinToString(separator = "/")
        val path =
            if (isBase)
                asPath(solutionsDir, repoFullName, baseDir, pathBeforeFileName)
            else
                asPath(solutionsDir, repoFullName, creator, pathBeforeFileName)
        return path to pathElements.last()
    }

}