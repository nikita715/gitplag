package io.gitplag.git.rest

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.github.kittinunf.fuel.core.FuelError
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.entity.Repository
import io.gitplag.model.repo.SolutionFileRecordRepository
import io.gitplag.util.sendRestRequest

/**
 * Loader of files from Gitlab
 */
class GitlabRestManager(
    sourceCodeStorage: SourceCodeStorage,
    solutionFileRecordRepository: SolutionFileRecordRepository,
    private val accessToken: String
) : AbstractGitRestManager(sourceCodeStorage, solutionFileRecordRepository, accessToken) {

    override fun getBranchOfRepo(repo: Repository, name: String) =
        sendRestRequest<JsonObject>(
            url = "https://gitlab.com/api/v4/projects/${repo.gitId}/repository/branches/$name",
            accessToken = accessToken
        )

    override fun findBranchesOfRepo(repo: Repository): List<String> =
        sendRestRequest<JsonArray<JsonObject>>(
            url = "https://gitlab.com/api/v4/projects/${repo.gitId}/repository/branches",
            accessToken = accessToken
        ).map { requireNotNull(it.string("name")) }

    override fun findPullRequests(repo: Repository, page: Int): JsonArray<JsonObject> =
        sendRestRequest(
            url = "https://gitlab.com/api/v4/projects/${repo.gitId}/merge_requests?page=$page&state=all",
            accessToken = accessToken
        )

    override fun linkToRepoArchive(repoName: String, branchName: String): String {
        val onlyRepoName = repoName.substringAfter("/")
        return "https://gitlab.com/$repoName/-/archive/$branchName/$onlyRepoName-$branchName.zip"
    }

    override fun getRepoIdByName(repoName: String) =
        try {
            sendRestRequest<JsonObject>(
                url = "https://gitlab.com/api/v4/projects/${repoName.replaceFirst("/", "%2F")}",
                accessToken = accessToken
            ).long("id")?.toString()
        } catch (e: FuelError) {
            null
        }

    /**
     * Get gitlab repo name by the [id]
     */
    fun repoNameById(id: String?) = sendRestRequest<JsonObject>("https://gitlab.com/api/v4/projects/$id")
        .string("path_with_namespace")

}