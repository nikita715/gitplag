package ru.nikstep.redink.analysis

import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository
import java.io.File
import java.nio.file.Paths

class FileSystemSolutionLoadingService(
    private val sourceCodeRepository: SourceCodeRepository,
    private val repositoryRepository: RepositoryRepository
) : SolutionLoadingService {

    override fun loadSolutions(repoName: String, fileName: String): Pair<File, List<File>> {
        val solutionDirectory = File(Paths.get("solutions", repoName).toUri())
        val directories = solutionDirectory.list()
        return File("") to directories.mapNotNull {
            val file = File(Paths.get("solutions", repoName, it, fileName).toUri())
            if (file.exists()) file else null
        }
    }
}