package ru.nikstep.redink.github.service

import ru.nikstep.redink.data.PullRequestData
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

    override fun load(repoId: Long, fileName: String): List<File> {
        val repository = repositoryRepository.findById(repoId).get()
        val solutionDirectory = File(Paths.get(repository.name).toUri())
        val directories = solutionDirectory.list()
        return directories.map { File(Paths.get(repository.name, it, fileName).toUri()) }
    }

    override fun save(prData: PullRequestData, fileName: String, fileText: String) {
        val pathToFile = getPathToFile(prData.repoFullName, prData.creatorName, fileName)
        val tempDirectory =
            Files.createDirectories(Paths.get(pathToFile.first))
        val path = Paths.get(tempDirectory.toString(), pathToFile.second)
        Files.deleteIfExists(path)
        val tempFile = Files.createFile(path)
        FileOutputStream(tempFile.toFile()).use { fileOutputStream -> fileOutputStream.write(fileText.toByteArray()) }
    }

    override fun load(userId: Long, repoId: Long, fileName: String): File {
        val repository = repositoryRepository.findById(repoId).get()
        val user = userRepository.findById(userId).get()
        val pathToFile = Paths.get(repository.name, user.name, fileName)
        return File(pathToFile.toUri())
    }

    private fun getPathToFile(repoFullName: String, creator: String, fileName: String): Pair<String, String> {
        val fullPath = fileName.split("/")
        val split = fullPath.dropLast(1).joinToString(separator = "/")
        return "solutions/$repoFullName/$creator/$split" to fullPath.last()
    }
}
