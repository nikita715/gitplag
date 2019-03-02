package ru.nikstep.redink.github

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.github.temporary.ChangeLoader
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.GitProperty.GITLAB

class GitlabWebhookService(
    pullRequestRepository: PullRequestRepository,
    private val changeLoader: ChangeLoader
) :
    AbstractWebhookService(pullRequestRepository) {

    override val JsonObject.gitService: GitProperty
        get() = GITLAB

    override val JsonObject.repoId: Long?
        get() = obj("project")?.long("id")

    override val JsonObject.number: Int?
        get() = obj("object_attributes")?.int("iid")

    override val JsonObject.repoFullName: String?
        get() = obj("project")?.string("path_with_namespace")

    override val JsonObject.creatorName: String?
        get() = obj("user")?.string("username")

    override val JsonObject.headSha: String?
        get() = obj("object_attributes")?.obj("last_commit")?.string("id")

    override val JsonObject.branchName: String?
        get() = obj("object_attributes")?.string("source_branch")

    override val JsonObject.changedFiles: List<String>
        get() = changeLoader.loadChanges(repoId!!, repoFullName!!, number!!, headSha!!, secretKey!!)
}