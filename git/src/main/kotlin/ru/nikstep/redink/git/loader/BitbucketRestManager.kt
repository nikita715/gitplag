package ru.nikstep.redink.git.loader

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.sendRestRequest

/**
 * Loader of files from Bitbucket
 */
class BitbucketRestManager(
    solutionStorage: SolutionStorage
) : AbstractGitRestManager(solutionStorage) {

    override fun findBranchesOfRepo(repo: Repository) =
        requireNotNull(sendRestRequest<JsonObject>("https://api.bitbucket.org/2.0/repositories/nikita715/plagiarism_test2/refs/branches")
            .array<JsonObject>("values")?.map { requireNotNull(it.string("name")) })

    override fun findPullRequests(repo: Repository) =
        requireNotNull(
            sendRestRequest<JsonObject>("https://api.bitbucket.org/2.0/repositories/${repo.name}/pullrequests")
                .array<JsonObject>("values")
        )

    override fun linkToRepoArchive(repoName: String, branchName: String): String =
        "https://bitbucket.org/$repoName/get/$branchName.zip"


}
