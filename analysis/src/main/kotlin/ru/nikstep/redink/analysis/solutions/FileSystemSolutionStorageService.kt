package ru.nikstep.redink.analysis.solutions

import mu.KotlinLogging
import ru.nikstep.redink.analysis.PreparedAnalysisFiles
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.RepositoryRepository
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

class FileSystemSolutionStorageService(
    private val repositoryRepository: RepositoryRepository
) : SolutionStorageService {
    private val logger = KotlinLogging.logger {}

    @Synchronized
    override fun loadAllBasesAndSolutions(prData: PullRequest): Collection<PreparedAnalysisFiles> {
        val requiredFiles =
            prData.changedFiles.intersect(repositoryRepository.findByName(prData.repoFullName).filePatterns)
        return requiredFiles.map { fileName -> loadBaseAndSolutions(prData.repoFullName, fileName) }
    }

    @Synchronized
    override fun loadBaseAndSolutions(repoName: String, fileName: String): PreparedAnalysisFiles {
        val baseFile = loadBase(repoName, fileName)
        val solutionFiles = loadSolutionFiles(repoName, fileName)
        logger.info { "File storage: loaded files $repoName/**/$fileName" }
        return PreparedAnalysisFiles(fileName, baseFile, solutionFiles)
    }

    override fun loadBase(repoName: String, fileName: String): File {
        return File(Paths.get("base", repoName, fileName).toUri())
    }

    private fun loadSolutionFiles(repoName: String, fileName: String): List<File> {
        val solutionDirectory = File(Paths.get("solutions", repoName).toUri())
        val directories = solutionDirectory.list()
        return directories.mapNotNull {
            val file = File(Paths.get("solutions", repoName, it, fileName).toUri())
            if (file.exists()) file else null
        }
    }

    @Synchronized
    override fun saveSolution(prData: PullRequest, fileName: String, fileText: String): File {
        val pathToFile = getPathToFile(prData.repoFullName, prData.creatorName, fileName)
        return save(pathToFile, fileText)
    }

    override fun saveBase(prData: PullRequest, fileName: String, fileText: String): File {
        return saveBase(prData.repoFullName, fileName, fileText)
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
        val pathToFile = Paths.get(repoName, userName, fileName)
        logger.info { "File storage: loaded file ${repoName}/${userName}/$fileName" }
        return File(pathToFile.toUri())
    }

    private fun getPathToFile(
        repoFullName: String,
        repoFilePath: String,
        creator: String = "",
        isBase: Boolean = false
    ): Pair<String, String> {
        val fullPath = repoFilePath.split("/")
        val path = fullPath.dropLast(1).joinToString(separator = "/")
        return "${if (isBase) "base" else "solutions"}/$repoFullName/${if (isBase) "" else "$creator/"}$path" to fullPath.last()
    }

}