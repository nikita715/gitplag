package ru.nikstep.redink.analysis.solutions

import mu.KotlinLogging
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.SourceCode
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.model.repo.UserRepository
import java.io.File
import java.util.*

class PostgresqlSolutionService(
    private val sourceCodeRepository: SourceCodeRepository,
    private val userRepository: UserRepository,
    private val repositoryRepository: RepositoryRepository
) : DatabaseSolutionService {

    override fun load(repoName: String, fileName: String): Pair<Pair<String, File>, List<File>> {
        TODO("not implemented")
    }

    private val logger = KotlinLogging.logger {}

    @Synchronized
    override fun save(prData: PullRequest, fileName: String, fileText: String) {
        val user = userRepository.findByName(prData.creatorName)
        val repo = repositoryRepository.findByName(prData.repoFullName)

        val sourceCode = sourceCodeRepository.findByUserIdAndRepoIdAndFileName(user.id, repo.id, fileName)

        if (sourceCode != null) {
            sourceCodeRepository.delete(sourceCode)
        }

        val bytes = Base64.getEncoder().encode(fileText.toByteArray())
        sourceCodeRepository.save(SourceCode(user = user, repo = repo, fileName = fileName, fileText = bytes))

        logger.info {
            "SourceCode: saved $fileName, user ${prData.creatorName}, repository ${prData.repoFullName}, " +
                    "url https://github.com/${prData.repoFullName}/blob/${prData.headSha}/$fileName"
        }
    }

    override fun load(userId: Long, repoId: Long, fileName: String): File {
        TODO("not implemented")
    }

}