package io.gitplag.git.rest

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.github.kittinunf.fuel.core.FuelError
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.entity.Repository
import io.gitplag.util.sendRestRequest

/**
 * Loader of files from Github
 */
class GithubRestManager(
    sourceCodeStorage: SourceCodeStorage,
    private val accessToken: String
) : AbstractGitRestManager(sourceCodeStorage) {

    override fun getBranchOfRepo(repo: Repository, name: String) =
        sendRestRequest<JsonObject>(
            url = "https://api.github.com/repos/${repo.name}/branches/$name",
            accessToken = accessToken
        )

    override fun findPullRequests(repo: Repository, page: Int) =
        sendRestRequest<JsonArray<JsonObject>>(
            url = "https://api.github.com/repos/${repo.name}/pulls?page=$page&state=all",
            accessToken = accessToken
        )

    override fun linkToRepoArchive(repoName: String, branchName: String): String =
        "https://github.com/$repoName/archive/$branchName.zip"

    override fun findBranchesOfRepo(repo: Repository): List<String> =
        sendRestRequest<JsonArray<JsonObject>>(
            url = "https://api.github.com/repos/${repo.name}/branches",
            accessToken = accessToken
        ).map { requireNotNull(it.string("name")) }

    override fun getRepoIdByName(repoName: String): String? =
        try {
            sendRestRequest<JsonObject>(
                url = "https://api.github.com/repos/$repoName",
                accessToken = accessToken
            ).long("id")?.toString()
        } catch (e: FuelError) {
            null
        }
}
