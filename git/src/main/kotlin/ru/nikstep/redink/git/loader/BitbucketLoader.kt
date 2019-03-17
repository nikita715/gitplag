package ru.nikstep.redink.git.loader

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.entity.SourceCode
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.util.sendRestRequest

/**
 * Loader of files from Bitbucket
 */
class BitbucketLoader(
    solutionStorage: SolutionStorage,
    repositoryRepository: RepositoryRepository
) : AbstractGitLoader(solutionStorage, repositoryRepository) {
    override fun loadChangedFilesOfCommit(repoName: String, headSha: String): List<String> {
        TODO("not implemented")
    }

    override fun loadChangedFiles(pullRequest: PullRequest): List<String> =
        pullRequest.run {
            requireNotNull(sendRestRequest<JsonObject>(
                "https://api.bitbucket.org/2.0/repositories/$mainRepoFullName/pullrequests/$number/diffstat"
            ).array<JsonObject>("values")?.map { requireNotNull(it.obj("new")?.string("path")) })
        }


    override fun loadFileText(repoFullName: String, branchName: String, fileName: String, secretKey: String): String =
        sendRestRequest("https://bitbucket.org/$repoFullName/raw/$branchName/$fileName")

    override fun loadFilesOfRepository(repo: Repository): List<SourceCode> {
        TODO("not implemented")
    }
}
