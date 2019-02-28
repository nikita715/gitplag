package ru.nikstep.redink.github

import org.springframework.boot.configurationprocessor.json.JSONObject
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.Git.GITLAB
import ru.nikstep.redink.util.RequestUtil.Companion.sendRestRequest

class GitlabPullRequestWebhookService(private val pullRequestRepository: PullRequestRepository) : WebhookService {

    override fun saveNewPullRequest(payload: String) {
        JSONObject(payload).let {
            val attributes = it.getJSONObject("object_attributes")
            val repoId = it.getJSONObject("project").getLong("id")
            val mrNumber = attributes.getInt("iid")
            val changes = loadMrChanges(repoId, mrNumber).getJSONArray("changes").let { changes ->
                (0 until changes.length()).map { i ->
                    val change = changes[i] as JSONObject
                    change.getString("new_path")
                }
            }
            PullRequest(
                gitService = GITLAB,
                repoId = repoId,
                repoFullName = it.getJSONObject("project").getString("path_with_namespace"),
                number = mrNumber,
                creatorName = it.getJSONObject("user").getString("username"),
                headSha = attributes.getJSONObject("last_commit").getString("id"),
                branchName = attributes.getString("source_branch"),
                changedFiles = changes
            )
        }.also { pullRequestRepository.save(it) }
    }

    fun loadMrChanges(repoId: Long, mrNumber: Int): JSONObject {
        return sendRestRequest("https://gitlab.com/api/v4/projects/$repoId/merge_requests/$mrNumber/changes") as JSONObject
    }
}