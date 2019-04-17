package io.gitplag.core.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import io.gitplag.git.payload.PayloadProcessor
import io.gitplag.git.rest.GitRestManager
import io.gitplag.model.entity.BaseFileRecord
import io.gitplag.model.entity.SolutionFileRecord
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.manager.RepositoryDataManager
import io.gitplag.model.repo.BaseFileRecordRepository
import io.gitplag.model.repo.SolutionFileRecordRepository
import org.springframework.beans.factory.annotation.Qualifier

class SourceFileQueries(
    private val solutionFileRecordRepository: SolutionFileRecordRepository,
    private val baseFileRecordRepository: BaseFileRecordRepository,
    private val repositoryDataManager: RepositoryDataManager,
    @Qualifier("gitRestManagers") private val restManagers: Map<GitProperty, GitRestManager>,
    @Qualifier("payloadProcessors") private val payloadProcessors: Map<GitProperty, PayloadProcessor>
) : GraphQLQueryResolver {

    fun getLocalBases(
        git: GitProperty, repoFullName: String, branch: String?, fileName: String?
    ): List<BaseFileRecord>? {
        val repo = repositoryDataManager.findByGitServiceAndName(git, repoFullName)
        return if (repo != null) baseFileRecordRepository.findAllByRepo(repo)
            .filter {
                ((branch == null) || (it.branch == branch)) && ((fileName == null) || (it.fileName == fileName))
            }
        else null
    }

    fun getLocalSolutions(
        git: GitProperty, repoFullName: String, branch: String?,
        student: String?, fileName: String?
    ): List<SolutionFileRecord>? {
        val repo = repositoryDataManager.findByGitServiceAndName(git, repoFullName)
        return if (repo != null) solutionFileRecordRepository.findAllByRepo(repo)
            .filter {
                ((branch == null) || (it.pullRequest.sourceBranchName == branch))
                        && ((fileName == null) || (it.fileName == fileName))
                        && ((student == null) || (it.pullRequest.creatorName == student))
            }
        else null
    }

    fun updateFilesOfRepo(git: GitProperty, repoFullName: String): ComposedFiles? {
        val repository = repositoryDataManager.findByGitServiceAndName(git, repoFullName)

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

    data class ComposedFiles(val bases: List<BaseFileRecord>, val solutions: List<SolutionFileRecord>)

}