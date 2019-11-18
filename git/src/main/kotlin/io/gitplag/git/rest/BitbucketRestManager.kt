package io.gitplag.git.rest

import com.beust.klaxon.JsonObject
import com.github.kittinunf.fuel.core.FuelError
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.entity.Repository
import io.gitplag.model.repo.SolutionFileRecordRepository
import io.gitplag.util.sendRestRequest

/**
 * Loader of files from Bitbucket
 */
class BitbucketRestManager(
    sourceCodeStorage: SourceCodeStorage,
    solutionFileRecordRepository: SolutionFileRecordRepository,
    private val accessToken: String
) : AbstractGitRestManager(sourceCodeStorage, solutionFileRecordRepository, accessToken) {

    override fun getBranchOfRepo(repo: Repository, name: String) =
        sendRestRequest<JsonObject>(
            url = "https://api.bitbucket.org/2.0/repositories/${repo.name}/refs/branches/$name",
            accessToken = accessToken
        )

    override fun findBranchesOfRepo(repo: Repository) =
        requireNotNull(
            sendRestRequest<JsonObject>(
                url = "https://api.bitbucket.org/2.0/repositories/${repo.name}/refs/branches",
                accessToken = accessToken
            ).array<JsonObject>("values")?.map { requireNotNull(it.string("name")) })

    override fun findPullRequests(repo: Repository, page: Int) =
        requireNotNull(
            sendRestRequest<JsonObject>(
                url = "https://api.bitbucket.org/2.0/repositories/${repo.name}/pullrequests?page=$page",
                accessToken = accessToken
            ).array<JsonObject>("values")
        )

    override fun linkToRepoArchive(repoName: String, branchName: String): String =
        "https://bitbucket.org/$repoName/get/$branchName.zip"

    override fun getRepoIdByName(repoName: String): String? =
        try {
            sendRestRequest<JsonObject>(
                url = "https://api.bitbucket.org/2.0/repositories/$repoName",
                accessToken = accessToken
            ).string("uuid")
        } catch (e: FuelError) {
            null
        }

}
