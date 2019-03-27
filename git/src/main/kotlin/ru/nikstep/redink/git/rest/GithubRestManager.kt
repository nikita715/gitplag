package ru.nikstep.redink.git.rest

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import ru.nikstep.redink.analysis.solutions.SourceCodeStorage
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.sendRestRequest

/**
 * Loader of files from Github
 */
class GithubRestManager(
    sourceCodeStorage: SourceCodeStorage
) : AbstractGitRestManager(sourceCodeStorage) {

    override fun findPullRequests(repo: Repository, page: Int) =
        sendRestRequest<JsonArray<JsonObject>>("https://api.github.com/repos/${repo.name}/pulls?page=$page&state=all")

    override fun linkToRepoArchive(repoName: String, branchName: String): String =
        "https://github.com/$repoName/archive/$branchName.zip"

    override fun findBranchesOfRepo(repo: Repository): List<String> =
        sendRestRequest<JsonArray<JsonObject>>("https://api.github.com/repos/${repo.name}/branches")
            .map { requireNotNull(it.string("name")) }

}
