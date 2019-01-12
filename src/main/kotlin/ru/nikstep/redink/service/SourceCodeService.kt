package ru.nikstep.redink.service

import mu.KotlinLogging
import ru.nikstep.redink.data.PullRequestData
import ru.nikstep.redink.entity.SourceCode
import ru.nikstep.redink.repo.RepositoryRepository
import ru.nikstep.redink.repo.SourceCodeRepository
import ru.nikstep.redink.repo.UserRepository

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

        var sourceCode = sourceCodeRepository.findByUserAndRepoAndFileName(user, repo, fileName)

        if (sourceCode == null) {
            sourceCode = SourceCode(user, repo, fileName, fileText)
        } else {
            sourceCode.fileText = fileText
        }

        sourceCodeRepository.save(sourceCode)

        logger.info {
            "SourceCode: saved $fileName, user ${prData.creatorName}, repository ${prData.repoFullName}, " +
                    "url https://github.com/${prData.repoFullName}/blob/${prData.headSha}/$fileName"
        }
    }

}