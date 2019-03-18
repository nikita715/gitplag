package ru.nikstep.redink.git.loader

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.SolutionFileRecord
import ru.nikstep.redink.util.sendRestRequest

/**
 * Loader of files from Bitbucket
 */
class BitbucketLoader(
    solutionStorage: SolutionStorage
) : AbstractGitLoader(solutionStorage) {
    override fun loadFilesOfPullRequest(pullRequest: PullRequest) {
        TODO("not implemented")
    }

    override fun loadChangedFilesOfCommit(repoName: String, headSha: String): List<String> {
        TODO("not implemented")
    }

    override fun loadChangedFiles(pullRequest: PullRequest): List<String> =
        pullRequest.run {
            requireNotNull(sendRestRequest<JsonObject>(
                "https://api.bitbucket.org/2.0/repositories/${pullRequest.repo.name}/pullrequests/$number/diffstat"
            ).array<JsonObject>("values")?.map { requireNotNull(it.obj("new")?.string("path")) })
        }


    override fun loadFileText(repoFullName: String, branchName: String, fileName: String): String =
        sendRestRequest("https://bitbucket.org/$repoFullName/raw/$branchName/$fileName")

    override fun loadRepositoryAndPullRequestFiles(repo: Repository): List<SolutionFileRecord> {
        TODO("not implemented")
    }
}
