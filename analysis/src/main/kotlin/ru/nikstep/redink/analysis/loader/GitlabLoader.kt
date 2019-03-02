package ru.nikstep.redink.analysis.loader

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.sendRestRequest

class GitlabLoader(
    solutionStorage: SolutionStorage,
    repositoryRepository: RepositoryRepository
) : AbstractGitLoader(solutionStorage, repositoryRepository) {

    override fun loadChangedFiles(pullRequest: PullRequest): List<String> =
        pullRequest.run {
            sendRestRequest<JsonObject>(
                "https://gitlab.com/api/v4/projects/$repoId/merge_requests/$number/changes"
            ).array<JsonObject>("changes")!!.map { change ->
                change.string("new_path")!!

            }
        }

    override fun toFileQuery(repoName: String, branchName: String, fileName: String): String {
        return "https://gitlab.com/$repoName/raw/$branchName/$fileName"
    }
}