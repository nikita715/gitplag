package ru.nikstep.redink.github

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.github.temporary.ChangeLoader
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.GitProperty.BITBUCKET

class BitbucketWebhookService(
    pullRequestRepository: PullRequestRepository,
    private val changeLoader: ChangeLoader
) :
    AbstractWebhookService(pullRequestRepository) {

    override val JsonObject.gitService: GitProperty
        get() = BITBUCKET

    override val JsonObject.repoId: Long?
        get() = -1

    override val JsonObject.number: Int?
        get() = obj("pullrequest")?.int("id")

    override val JsonObject.repoFullName: String?
        get() = obj("pullrequest")?.obj("destination")?.obj("repository")?.string("full_name")

    override val JsonObject.creatorName: String?
        get() = obj("pullrequest")?.obj("author")?.string("username")

    override val JsonObject.headSha: String?
        get() = obj("pullrequest")?.obj("source")?.obj("commit")?.string("hash")

    override val JsonObject.branchName: String?
        get() = obj("pullrequest")?.obj("source")?.obj("branch")?.string("name")

    override val JsonObject.changedFiles: List<String>
        get() = changeLoader.loadChanges(repoId!!, repoFullName!!, number!!, headSha!!, secretKey!!)

}
