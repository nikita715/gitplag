package ru.nikstep.redink.analysis.solutions

import mu.KotlinLogging
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.UserRepository
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths


class FileSystemSolutionService(
    private val repositoryRepository: RepositoryRepository,
    private val userRepository: UserRepository
) : SolutionService {
    private val logger = KotlinLogging.logger {}

    @Synchronized
    override fun load(repoName: String, fileName: String): Pair<Pair<String, File>, List<File>> {
        val baseFile = loadBaseFile(repoName, fileName)
        val solutionFiles = loadSolutionFiles(repoName, fileName)
        logger.info { "File storage: loaded files $repoName/**/$fileName" }
        return (fileName to baseFile) to solutionFiles
    }

    private fun loadBaseFile(repoName: String, fileName: String): File {
        val baseFile = File(Paths.get("base", repoName, fileName).toUri())
        if (baseFile.notExists()) {
            throw RuntimeException("No base file $fileName of $repoName repo")
        }
        return baseFile
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
    override fun save(prData: PullRequest, fileName: String, fileText: String) {
        val pathToFile = getPathToFile(prData.repoFullName, prData.creatorName, fileName)
        val tempDirectory =
            Files.createDirectories(Paths.get(pathToFile.first))
        val path = Paths.get(tempDirectory.toString(), pathToFile.second)
        Files.deleteIfExists(path)
        val tempFile = Files.createFile(path)
        FileOutputStream(tempFile.toFile()).use { fileOutputStream -> fileOutputStream.write(fileText.toByteArray()) }
        logger.info { "File storage: saved file ${pathToFile.first}/${pathToFile.second}" }
    }

    @Synchronized
    override fun load(userId: Long, repoId: Long, fileName: String): File {
        val repository = repositoryRepository.findById(repoId).get()
        val user = userRepository.findById(userId).get()
        val pathToFile = Paths.get(repository.name, user.name, fileName)
        logger.info { "File storage: loaded file ${repository.name}/${user.name}/$fileName" }
        return File(pathToFile.toUri())
    }

    private fun getPathToFile(repoFullName: String, creator: String, fileName: String): Pair<String, String> {
        val fullPath = fileName.split("/")
        val split = fullPath.dropLast(1).joinToString(separator = "/")
        return "solutions/$repoFullName/$creator/$split" to fullPath.last()
    }

}

fun File.notExists(): Boolean {
    return !this.exists()
}