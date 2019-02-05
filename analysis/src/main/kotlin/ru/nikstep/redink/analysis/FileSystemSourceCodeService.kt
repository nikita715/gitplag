package ru.nikstep.redink.analysis

import mu.KotlinLogging
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.UserRepository
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths


class FileSystemSourceCodeService(
    private val repositoryRepository: RepositoryRepository,
    private val userRepository: UserRepository
) : SourceCodeService {
    private val logger = KotlinLogging.logger {}

    override fun load(repoName: String, fileName: String): Pair<File, List<File>> {
        val solutionDirectory = File(Paths.get("solutions", repoName).toUri())
        val directories = solutionDirectory.list()
        logger.info { "File storage: loaded files $repoName/**/$fileName" }
        return File("") to directories.mapNotNull {
            val file = File(Paths.get("solutions", repoName, it, fileName).toUri())
            if (file.exists()) file else null
        }
    }

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
