package io.gitplag.git.rest

import com.beust.klaxon.JsonObject
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.entity.Repository
import io.gitplag.util.sendRestRequest

/**
 * Loader of files from Bitbucket
 */
class BitbucketRestManager(
    sourceCodeStorage: SourceCodeStorage
) : AbstractGitRestManager(sourceCodeStorage) {
    override fun getBranchOfRepo(repo: Repository, name: String): JsonObject {
        TODO("not implemented")
    }

    override fun findBranchesOfRepo(repo: Repository) =
        requireNotNull(
            sendRestRequest<JsonObject>("https://api.bitbucket.org/2.0/repositories/nikita715/plagiarism_test2/refs/branches")
                .array<JsonObject>("values")?.map { requireNotNull(it.string("name")) })

    override fun findPullRequests(repo: Repository, page: Int) =
        requireNotNull(
            sendRestRequest<JsonObject>("https://api.bitbucket.org/2.0/repositories/${repo.name}/pullrequests")
                .array<JsonObject>("values")
        )

    override fun linkToRepoArchive(repoName: String, branchName: String): String =
        "https://bitbucket.org/$repoName/get/$branchName.zip"


}
