package ru.nikstep.redink.github

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.GitProperty.GITLAB
import ru.nikstep.redink.util.JsonObjectDeserializer
import ru.nikstep.redink.util.sendRestRequest

class GitlabWebhookService(pullRequestRepository: PullRequestRepository) :
    AbstractWebhookService(pullRequestRepository) {

    override val JsonObject.gitService: GitProperty
        get() = GITLAB

    override val JsonObject.repoId: Long
        get() = obj("project")!!.long("id")!!

    override val JsonObject.number: Int
        get() = obj("object_attributes")!!.int("iid")!!

    override val JsonObject.repoFullName: String
        get() = obj("project")!!.string("path_with_namespace")!!

    override val JsonObject.creatorName: String
        get() = obj("user")!!.string("username")!!

    override val JsonObject.headSha: String
        get() = obj("object_attributes")!!.obj("last_commit")!!.string("id")!!

    override val JsonObject.branchName: String
        get() = obj("object_attributes")!!.string("source_branch")!!

    override val JsonObject.changedFiles: List<String>
        get() = sendRestRequest(
            "https://gitlab.com/api/v4/projects/$repoId/merge_requests/$number/changes",
            deserializer = JsonObjectDeserializer
        ).array<JsonObject>("changes")!!.map { change ->
            change.string("new_path")!!
        }
}