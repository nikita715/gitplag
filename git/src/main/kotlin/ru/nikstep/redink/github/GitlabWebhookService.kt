package ru.nikstep.redink.github

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty.GITLAB
import ru.nikstep.redink.util.JsonObjectDeserializer
import ru.nikstep.redink.util.sendRestRequest

class GitlabWebhookService(pullRequestRepository: PullRequestRepository) :
    AbstractWebhookService(pullRequestRepository) {

    override val jsonToPullRequest: (JsonObject) -> PullRequest = { jsonPayload ->
        val attributes = jsonPayload.obj("object_attributes")!!
        val repoId = jsonPayload.obj("project")!!.long("id")!!
        val mrNumber = attributes.int("iid")!!
        val changes = loadMrChanges(repoId, mrNumber).array<JsonObject>("changes")!!.map { change ->
            change.string("new_path")!!
        }
        PullRequest(
            gitService = GITLAB,
            repoId = repoId,
            repoFullName = jsonPayload.obj("project")!!.string("path_with_namespace")!!,
            number = mrNumber,
            creatorName = jsonPayload.obj("user")!!.string("username")!!,
            headSha = attributes.obj("last_commit")!!.string("id")!!,
            branchName = attributes.string("source_branch")!!,
            changedFiles = changes
        )
    }

    fun loadMrChanges(repoId: Long, mrNumber: Int): JsonObject {
        return sendRestRequest(
            "https://gitlab.com/api/v4/projects/$repoId/merge_requests/$mrNumber/changes",
            deserializer = JsonObjectDeserializer
        )
    }
}