package io.gitplag.git.rest

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.entity.Repository
import io.gitplag.util.sendRestRequest

/**
 * Loader of files from Gitlab
 */
class GitlabRestManager(
    sourceCodeStorage: SourceCodeStorage
) : AbstractGitRestManager(sourceCodeStorage) {
    override fun getBranchOfRepo(repo: Repository, name: String): JsonObject {
        TODO("not implemented")
    }

    override fun findBranchesOfRepo(repo: Repository): List<String> =
        sendRestRequest<JsonArray<JsonObject>>("https://gitlab.com/api/v4/projects/${repo.gitId}/repository/branches")
            .map { requireNotNull(it.string("name")) }


    override fun findPullRequests(repo: Repository, page: Int): JsonArray<JsonObject> =
        sendRestRequest("https://gitlab.com/api/v4/projects/${repo.gitId}/merge_requests")

    override fun linkToRepoArchive(repoName: String, branchName: String): String {
        val onlyRepoName = repoName.substringAfter("/")
        return "https://gitlab.com/$repoName/-/archive/$branchName/$onlyRepoName-$branchName.zip"
    }

}