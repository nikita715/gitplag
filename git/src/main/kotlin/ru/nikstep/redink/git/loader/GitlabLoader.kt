package ru.nikstep.redink.git.loader

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.SourceCode
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.sendRestRequest

/**
 * Loader of files from Gitlab
 */
class GitlabLoader(
    solutionStorage: SolutionStorage,
    repositoryRepository: RepositoryRepository
) : AbstractGitLoader(solutionStorage, repositoryRepository) {
    override fun loadChangedFilesOfCommit(repoName: String, headSha: String): List<String> {
        TODO("not implemented")
    }

    override fun loadChangedFiles(pullRequest: PullRequest): List<String> =
        pullRequest.run {
            requireNotNull(sendRestRequest<JsonObject>(
                "https://gitlab.com/api/v4/projects/$mainRepoId/merge_requests/$number/changes"
            ).array<JsonObject>("changes")?.map { change ->
                requireNotNull(change.string("new_path"))
            })
        }

    override fun loadFileText(repoFullName: String, branchName: String, fileName: String): String =
        sendRestRequest("https://gitlab.com/$repoFullName/raw/$branchName/$fileName")

    override fun loadFilesOfRepository(repo: Repository): List<SourceCode> {
        TODO("not implemented")
    }
}