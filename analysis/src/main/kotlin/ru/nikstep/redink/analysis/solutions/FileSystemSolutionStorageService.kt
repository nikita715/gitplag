package ru.nikstep.redink.analysis.solutions

import mu.KotlinLogging
import ru.nikstep.redink.analysis.PreparedAnalysisFiles
import ru.nikstep.redink.model.entity.AnalysisPair
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.SourceCode
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

class FileSystemSolutionStorageService(
    private val sourceCodeRepository: SourceCodeRepository,
    private val repositoryRepository: RepositoryRepository
) : SolutionStorageService {
    private val logger = KotlinLogging.logger {}

    @Synchronized
    override fun loadAllBasesAndSolutions(pullRequest: PullRequest): Collection<PreparedAnalysisFiles> {
        val requiredFiles =
            pullRequest.changedFiles.intersect(repositoryRepository.findByName(pullRequest.repoFullName).filePatterns)
        return requiredFiles.map { fileName -> loadBaseAndSolutions(pullRequest.repoFullName, fileName) }
    }

    @Synchronized
    override fun loadBaseAndSolutions(repoName: String, fileName: String): PreparedAnalysisFiles {
        val baseFile = loadBase(repoName, fileName)
        val solutionFiles = loadSolutionFiles(repoName, fileName)
        logger.info { "File storage: loaded files $repoName/**/$fileName" }
        return PreparedAnalysisFiles(fileName, baseFile, solutionFiles)
    }

    override fun loadBase(repoName: String, fileName: String): File {
        return File(Paths.get("solutions", repoName, ".base", fileName).toUri())
    }

    private fun loadSolutionFiles(repoName: String, fileName: String): List<File> {
        val solutionDirectory = File(Paths.get("solutions", repoName).toUri())
        val directories = solutionDirectory.list().toList().intersect<String>(
            sourceCodeRepository.findAllByRepoAndFileName(repoName, fileName).map { it.user }
        )
        return directories.mapNotNull {
            val file = File(Paths.get("solutions", repoName, it, fileName).toUri())
            if (file.exists()) file else null
        }
    }

    @Synchronized
    override fun saveSolution(pullRequest: PullRequest, fileName: String, fileText: String): File {
        val pathToFile = getPathToFile(pullRequest.repoFullName, fileName, pullRequest.creatorName)
        sourceCodeRepository.save(
            SourceCode(
                user = pullRequest.creatorName,
                repo = pullRequest.repoFullName,
                fileName = fileName
            )
        )
        return save(pathToFile, fileText)
    }

    override fun loadSolution1(analysisPair: AnalysisPair): File {
        return loadSolution(
            analysisPair.repo,
            analysisPair.student1,
            analysisPair.fileName
        )
    }

    override fun loadSolution2(analysisPair: AnalysisPair): File {
        return loadSolution(
            analysisPair.repo,
            analysisPair.student2,
            analysisPair.fileName
        )
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
        val pathToFile = Paths.get("solutions", repoName, userName, fileName)
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
                "solutions/$repoFullName/.base/$pathBeforeFileName"
            else
                "solutions/$repoFullName/$creator/$pathBeforeFileName"
        return path to pathElements.last()
    }

}