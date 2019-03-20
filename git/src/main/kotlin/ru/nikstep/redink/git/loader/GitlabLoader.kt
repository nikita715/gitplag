package ru.nikstep.redink.git.loader

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.sendRestRequest

/**
 * Loader of files from Gitlab
 */
class GitlabLoader(
    solutionStorage: SolutionStorage
) : AbstractGitLoader(solutionStorage) {

    private val logger = KotlinLogging.logger {}

    override fun findBranchesOfRepo(repo: Repository): List<String> =
        sendRestRequest<JsonArray<JsonObject>>("https://gitlab.com/api/v4/projects/${repo.gitId}/repository/branches")
            .map { requireNotNull(it.string("name")) }


    override fun findPullRequests(repo: Repository): JsonArray<JsonObject> =
        sendRestRequest("https://gitlab.com/api/v4/projects/${repo.gitId}/merge_requests")

    override fun linkToRepoArchive(repoName: String, branchName: String): String {
        val onlyRepoName = repoName.substringAfter("/")
        return "https://gitlab.com/$repoName/-/archive/$branchName/$onlyRepoName-$branchName.zip"
    }

}