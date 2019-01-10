package ru.nikstep.redink.service

import ru.nikstep.redink.entity.SourceCode
import ru.nikstep.redink.repo.RepositoryRepository
import ru.nikstep.redink.repo.SourceCodeRepository
import ru.nikstep.redink.repo.UserRepository

class SourceCodeService(
    val sourceCodeRepository: SourceCodeRepository,
    val userRepository: UserRepository,
    val repositoryRepository: RepositoryRepository
) {

    @Synchronized
    fun save(username: String, repositoryName: String, fileName: String, fileText: String) {
        val user = userRepository.findByName(username)
        val repo = repositoryRepository.findByName(repositoryName)

        var sourceCode = sourceCodeRepository.findByUserAndRepoAndFileName(user, repo, fileName)

        if (sourceCode == null) {
            sourceCode = SourceCode(user, repo, fileName, fileText)
        } else {
            sourceCode.fileText = fileText
        }

        sourceCodeRepository.save(sourceCode)
    }

}