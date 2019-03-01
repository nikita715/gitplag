package ru.nikstep.redink.github

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.Git.GITLAB
import ru.nikstep.redink.util.parseAsObject
import ru.nikstep.redink.util.sendRestRequest

class GitlabPullRequestWebhookService(private val pullRequestRepository: PullRequestRepository) : WebhookService {

    override fun saveNewPullRequest(payload: String) {
        payload.parseAsObject().let {
            val attributes = it.obj("object_attributes")!!
            val repoId = it.obj("project")!!.long("id")!!
            val mrNumber = attributes.int("iid")!!
            val changes = loadMrChanges(repoId, mrNumber).array<JsonObject>("changes")!!.map { change ->
                change.string("new_path")!!
            }
            PullRequest(
                gitService = GITLAB,
                repoId = repoId,
                repoFullName = it.obj("project")!!.string("path_with_namespace")!!,
                number = mrNumber,
                creatorName = it.obj("user")!!.string("username")!!,
                headSha = attributes.obj("last_commit")!!.string("id")!!,
                branchName = attributes.string("source_branch")!!,
                changedFiles = changes
            )
        }.let(pullRequestRepository::save)
    }

    fun loadMrChanges(repoId: Long, mrNumber: Int): JsonObject {
        return sendRestRequest("https://gitlab.com/api/v4/projects/$repoId/merge_requests/$mrNumber/changes") as JsonObject
    }
}