package ru.nikstep.redink.analysis.solutions

import mu.KotlinLogging
import ru.nikstep.redink.analysis.CommittedFile
import ru.nikstep.redink.analysis.PreparedAnalysisFiles
import ru.nikstep.redink.model.entity.AnalysisPair
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.SourceCode
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.util.asPath
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

class FileSystemSolutionStorage(
    private val sourceCodeRepository: SourceCodeRepository,
    private val repositoryRepository: RepositoryRepository
) : SolutionStorage {
    private val logger = KotlinLogging.logger {}
    private val solutionsDir = "solutions"
    private val baseDir = ".base"

    @Synchronized
    override fun loadAllBasesAndSolutions(pullRequest: PullRequest): Collection<PreparedAnalysisFiles> {
        val repoName = pullRequest.repoFullName
        val repo = repositoryRepository.findByName(repoName)
        val requiredFiles = repo.filePatterns

        return requiredFiles.map { fileName ->
            PreparedAnalysisFiles(
                repoName = repoName,
                fileName = fileName,
                language = repo.language,
                base = loadBase(repoName, fileName),
                solutions = loadSolutionFiles(repoName, fileName)
            )
        }.filter { it.solutions.size > 1 }
    }

    override fun loadBase(repoName: String, fileName: String): File {
        return File(Paths.get(solutionsDir, repoName, baseDir, fileName).toUri())
    }

    private fun loadSolutionFiles(repoName: String, fileName: String): Map<String, CommittedFile> {
        val solutionDirectory = File(Paths.get(solutionsDir, repoName).toUri())
        val existingDirectories = solutionDirectory.list().toList()
        return sourceCodeRepository.findAllByRepoAndFileName(repoName, fileName).filter {
            existingDirectories.contains(it.user)
        }.mapNotNull {
            val file = File(Paths.get(solutionsDir, repoName, it.user, fileName).toUri())
            if (file.exists()) it.user to CommittedFile(file, it.sha) else null
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