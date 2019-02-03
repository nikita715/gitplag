//package ru.nikstep.redink.github.service
//
//import mu.KotlinLogging
//import ru.nikstep.redink.data.PullRequestData
//import ru.nikstep.redink.model.entity.SourceCode
//import ru.nikstep.redink.model.repo.RepositoryRepository
//import ru.nikstep.redink.model.repo.SourceCodeRepository
//import ru.nikstep.redink.model.repo.UserRepository
//import java.util.*
//
//class PostgresqlSourceCodeService(
//    private val sourceCodeRepository: SourceCodeRepository,
//    private val userRepository: UserRepository,
//    private val repositoryRepository: RepositoryRepository
//) : DatabaseSourceCodeService {
//
//    private val logger = KotlinLogging.logger {}
//
//    @Synchronized
//    override fun save(prData: PullRequestData, fileName: String, fileText: String) {
//        val user = userRepository.findByName(prData.creatorName)
//        val repo = repositoryRepository.findByName(prData.repoFullName)
//
//        val sourceCode = sourceCodeRepository.findByUserIdAndRepoIdAndFileName(user.id, repo.id, fileName)
//
//        if (sourceCode != null) {
//            sourceCodeRepository.delete(sourceCode)
//        }
//
//        val bytes = Base64.getEncoder().encode(fileText.toByteArray())
//        sourceCodeRepository.save(SourceCode(user = user, repo = repo, fileName = fileName, fileText = bytes))
//
//        logger.info {
//            "SourceCode: saved $fileName, user ${prData.creatorName}, repository ${prData.repoFullName}, " +
//                    "url https://github.com/${prData.repoFullName}/blob/${prData.headSha}/$fileName"
//        }
//    }
//
//    override fun load(userId: Long, repoId: Long, fileName: String): SourceCode? {
//        val sourceCode = sourceCodeRepository.findByUserIdAndRepoIdAndFileName(userId, repoId, fileName)
//        return sourceCode
//    }
//
//}