package ru.nikstep.redink.analysis.solutions

import mu.KotlinLogging
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.CommittedFile
import ru.nikstep.redink.model.data.PreparedAnalysisFiles
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.SourceCode
import ru.nikstep.redink.model.repo.RepositoryRepository
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
    private val repositoryRepository: RepositoryRepository
) : SolutionStorage {

    override fun loadBase(gitProperty: GitProperty, repoName: String, fileName: String): File =
        File(
            Paths.get(
                solutionsDir,
                gitProperty.toString(),
                repoName,
                baseDir,
                fileName
            ).toUri()
        )

    private val logger = KotlinLogging.logger {}
    private val solutionsDir = "solutions"
    private val baseDir = ".base"

    @Synchronized
    override fun loadAllBasesAndSolutions(analysisSettings: AnalysisSettings) =
        PreparedAnalysisFiles(
            repoName = analysisSettings.repository.name,
            language = analysisSettings.language,
            analyser = analysisSettings.analyser,
            bases = loadBases(analysisSettings.repository),
            solutions = loadSolutionFiles(analysisSettings.repository)
        )

    override fun loadBases(repository: Repository): List<File> {
        return Files.walk(Paths.get(solutionsDir, repository.gitService.toString(), repository.name, baseDir))
            .filter { path -> Files.isRegularFile(path) }
            .map(Path::toFile).collect(toList())
    }

    private fun loadSolutionFiles(repository: Repository): Map<String, CommittedFile> {
        val solutionDirectory = File(Paths.get(solutionsDir, repository.gitService.toString(), repository.name).toUri())
        val existingDirectories = solutionDirectory.list().toList()
        return sourceCodeRepository.findAllByRepo(repository.name).filter {
            existingDirectories.contains(it.user)
        }.mapNotNull {
            val file = File(
                Paths.get(
                    solutionsDir,
                    repository.gitService.toString(),
                    repository.name,
                    it.user,
                    it.fileName
                ).toUri()
            )
            if (file.exists()) it.user to CommittedFile(file, it.sha, it.fileName) else null
        }.toMap()
    }

    @Synchronized
    override fun saveSolution(pullRequest: PullRequest, fileName: String, fileText: String): File {
        val pathToFile =
            getPathToFile(pullRequest.gitService, pullRequest.repoFullName, fileName, pullRequest.creatorName)
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

    @Synchronized
    override fun saveBase(pullRequest: PullRequest, fileName: String, fileText: String): File {
        return saveBase(pullRequest.gitService, pullRequest.repoFullName, fileName, fileText)
    }

    private fun saveBase(gitService: GitProperty, repoName: String, fileName: String, fileText: String): File {
        val pathToFile = getPathToFile(gitService, repoName, fileName, isBase = true)
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

    private fun getPathToFile(
        gitService: GitProperty,
        repoFullName: String,
        repoFilePath: String,
        creator: String = "",
        isBase: Boolean = false
    ): Pair<String, String> {
        val pathElements: List<String> = repoFilePath.split("/")
        val pathBeforeFileName: String = pathElements.dropLast(1).joinToString(separator = "/")
        val path =
            if (isBase)
                asPath(solutionsDir, gitService.toString(), repoFullName, baseDir, pathBeforeFileName)
            else
                asPath(solutionsDir, gitService.toString(), repoFullName, creator, pathBeforeFileName)
        return path to pathElements.last()
    }

}