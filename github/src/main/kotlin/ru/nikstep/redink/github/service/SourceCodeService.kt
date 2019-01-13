package ru.nikstep.redink.github.service

import mu.KotlinLogging
import ru.nikstep.redink.github.data.PullRequestData
import ru.nikstep.redink.model.entity.SourceCode
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.model.repo.UserRepository

class SourceCodeService(
    private val sourceCodeRepository: SourceCodeRepository,
    private val userRepository: UserRepository,
    private val repositoryRepository: RepositoryRepository
) {

    private val logger = KotlinLogging.logger {}

    @Synchronized
    fun save(prData: PullRequestData, fileName: String, fileText: String) {
        val user = userRepository.findByName(prData.creatorName)
        val repo = repositoryRepository.findByName(prData.repoFullName)

        val sourceCode = sourceCodeRepository.findByUserAndRepoAndFileName(user, repo, fileName)

        if (sourceCode != null) {
            sourceCodeRepository.delete(sourceCode)
        }

        sourceCodeRepository.save(SourceCode(user = user, repo = repo, fileName = fileName, fileText = fileText))

        logger.info {
            "SourceCode: saved $fileName, user ${prData.creatorName}, repository ${prData.repoFullName}, " +
                    "url https://github.com/${prData.repoFullName}/blob/${prData.headSha}/$fileName"
        }
    }

}