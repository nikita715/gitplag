package ru.nikstep.redink.git.rest

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.sendRestRequest

/**
 * Loader of files from Github
 */
class GithubRestManager(
    solutionStorage: SolutionStorage
) : AbstractGitRestManager(solutionStorage) {

    private val logger = KotlinLogging.logger {}

    override fun findPullRequests(repo: Repository, page: Int) =
        sendRestRequest<JsonArray<JsonObject>>("https://api.github.com/repos/${repo.name}/pulls?page=$page&state=all")

    override fun linkToRepoArchive(repoName: String, branchName: String): String =
        "https://github.com/$repoName/archive/$branchName.zip"

    override fun findBranchesOfRepo(repo: Repository): List<String> =
        sendRestRequest<JsonArray<JsonObject>>("https://api.github.com/repos/${repo.name}/branches")
            .map { requireNotNull(it.string("name")) }

}
