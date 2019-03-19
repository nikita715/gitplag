package ru.nikstep.redink.git.loader

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.downloadAndUnpackZip
import ru.nikstep.redink.util.sendRestRequest

/**
 * Loader of files from Gitlab
 */
class GitlabLoader(
    private val solutionStorage: SolutionStorage
) : AbstractGitLoader(solutionStorage) {

    private val logger = KotlinLogging.logger {}

    override fun linkToRepoArchive(repoName: String, branchName: String): String {
        val onlyRepoName = repoName.substringAfter("/")
        return "https://gitlab.com/$repoName/-/archive/$branchName/$onlyRepoName-$branchName.zip"
    }

    override fun loadChangedFilesOfCommit(repoName: String, headSha: String): List<String> {
        TODO("not implemented")
    }

    override fun loadChangedFiles(pullRequest: PullRequest): List<String> =
        pullRequest.run {
            requireNotNull(sendRestRequest<JsonObject>(
                "https://gitlab.com/api/v4/projects/$mainRepoId/merge_requests/$number/changes"
            ).array<JsonObject>("changes")?.map { change ->
                requireNotNull(change.string("new_path"))
            })
        }

    override fun loadFileText(repoFullName: String, branchName: String, fileName: String): String =
        sendRestRequest("https://gitlab.com/$repoFullName/raw/$branchName/$fileName")

    override fun cloneRepositoryAndPullRequests(repo: Repository) {
        val branches = mutableSetOf<String>()
        val toList =
            sendRestRequest<JsonArray<JsonObject>>("https://gitlab.com/api/v4/projects/${repo.gitId}/merge_requests")
                .forEach {
                    val sourceBranchName = requireNotNull(it.string("source_branch"))
                    val sourceRepoName = getRepoName(requireNotNull(it.long("source_project_id")))
                    val headSha = requireNotNull(it.string("sha"))
                    val creator = requireNotNull(it.obj("author")?.string("username"))

                    branches += sourceBranchName

                    logger.info { "Git: download zip archive of repo = $sourceRepoName, branch = $sourceBranchName" }
                    downloadAndUnpackZip(linkToRepoArchive(sourceRepoName, sourceBranchName)) { unpackedDir ->
                        solutionStorage.saveSolutionsFromDir(
                            "$unpackedDir/${sourceRepoName.substringAfter("/")}-$sourceBranchName",
                            repo, sourceBranchName, creator, headSha
                        )
                    }
                }

        loadBases(branches, repo)
        return toList
    }

    fun loadBases(branches: Set<String>, repo: Repository) {
        branches.forEach { branch ->
            logger.info { "Git: download zip archive of repo = ${repo.name}, branch = $branch" }
            try {
                downloadAndUnpackZip(linkToRepoArchive(repo.name, branch)) { unpackedDir ->
                    solutionStorage.saveBasesFromDir(
                        "$unpackedDir/${repo.name.substringAfter("/")}-$branch",
                        repo, branch
                    )
                }
            } catch (e: Exception) {
                logger.info { "No base $branch" }
            }
        }
    }

    private fun getRepoName(repoId: Long): String =
        requireNotNull(
            sendRestRequest<JsonObject>(
                "https://gitlab.com/api/v4/projects/$repoId"
            ).string("path_with_namespace")
        )

}