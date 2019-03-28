package ru.nikstep.redink.core.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import ru.nikstep.redink.git.rest.GitRestManager
import ru.nikstep.redink.git.webhook.PayloadProcessor
import ru.nikstep.redink.model.entity.BaseFileRecord
import ru.nikstep.redink.model.entity.SolutionFileRecord
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.manager.RepositoryDataManager
import ru.nikstep.redink.model.repo.BaseFileRecordRepository
import ru.nikstep.redink.model.repo.SolutionFileRecordRepository

@Component
class SourceFileQueries(
    private val solutionFileRecordRepository: SolutionFileRecordRepository,
    private val baseFileRecordRepository: BaseFileRecordRepository,
    private val repositoryDataManager: RepositoryDataManager,
    @Qualifier("gitRestManagers") private val restManagers: Map<GitProperty, GitRestManager>,
    @Qualifier("payloadProcessors") private val payloadProcessors: Map<GitProperty, PayloadProcessor>
) : GraphQLQueryResolver {

    fun getLocalBases(
        git: GitProperty, repoName: String, branch: String?, fileName: String?
    ): List<BaseFileRecord>? {
        val repo = repositoryDataManager.findByGitServiceAndName(git, repoName)
        return if (repo != null) baseFileRecordRepository.findAllByRepo(repo)
            .filter {
                when {
                    branch != null -> it.branch == branch
                    fileName != null -> it.fileName == fileName
                    else -> true
                }
            }
        else null
    }

    fun getLocalSolutions(
        git: GitProperty, repoName: String, branch: String?,
        student: String?, fileName: String?
    ): List<SolutionFileRecord>? {
        val repo = repositoryDataManager.findByGitServiceAndName(git, repoName)
        return if (repo != null) solutionFileRecordRepository.findAllByRepo(repo)
            .filter {
                when {
                    branch != null -> it.pullRequest.sourceBranchName == branch
                    student != null -> it.pullRequest.creatorName == student
                    fileName != null -> it.fileName == fileName
                    else -> true
                }
            }
        else null
    }

    fun updateFilesOfRepo(git: GitProperty, repoName: String): ComposedFiles? {
        val repository = repositoryDataManager.findByGitServiceAndName(git, repoName)

        return if (repository != null) {
            val gitRestManager = restManagers.getValue(git)
            val payloadProcessor = payloadProcessors.getValue(git)
            gitRestManager.cloneRepository(repository)
            payloadProcessor.downloadAllPullRequestsOfRepository(repository)
            ComposedFiles(
                bases = baseFileRecordRepository.findAllByRepo(repository),
                solutions = solutionFileRecordRepository.findAllByRepo(repository)
            )
        } else null
    }

    class ComposedFiles(val bases: List<BaseFileRecord>, val solutions: List<SolutionFileRecord>)

}