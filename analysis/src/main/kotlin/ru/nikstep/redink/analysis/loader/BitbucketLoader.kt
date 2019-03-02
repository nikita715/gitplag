package ru.nikstep.redink.analysis.loader

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.sendRestRequest

class BitbucketLoader(
    solutionStorage: SolutionStorage,
    repositoryRepository: RepositoryRepository
) : AbstractGitLoader(solutionStorage, repositoryRepository) {

    override fun loadChangedFiles(pullRequest: PullRequest): List<String> =
        pullRequest.run {
            sendRestRequest<JsonArray<*>>(
                url = "https://api.bitbucket.org/1.0/repositories/$repoFullName/changesets/$headSha/diffstat"
            ).map { (it as JsonObject).string("file")!! }
        }


    override fun toFileQuery(repoName: String, branchName: String, fileName: String): String {
        return "https://bitbucket.org/$repoName/raw/$branchName/$fileName"
    }
}
