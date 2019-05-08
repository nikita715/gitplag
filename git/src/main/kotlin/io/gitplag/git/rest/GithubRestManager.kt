package io.gitplag.git.rest

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.entity.Repository
import io.gitplag.util.sendRestRequest

/**
 * Loader of files from Github
 */
class GithubRestManager(
    sourceCodeStorage: SourceCodeStorage
) : AbstractGitRestManager(sourceCodeStorage) {

    override fun getBranchOfRepo(repo: Repository, name: String) =
        sendRestRequest<JsonObject>("https://api.github.com/repos/${repo.name}/branches/$name")

    override fun findPullRequests(repo: Repository, page: Int) =
        sendRestRequest<JsonArray<JsonObject>>("https://api.github.com/repos/${repo.name}/pulls?page=$page&state=all")

    override fun linkToRepoArchive(repoName: String, branchName: String): String =
        "https://github.com/$repoName/archive/$branchName.zip"

    override fun findBranchesOfRepo(repo: Repository): List<String> =
        sendRestRequest<JsonArray<JsonObject>>("https://api.github.com/repos/${repo.name}/branches")
            .map { requireNotNull(it.string("name")) }
}
