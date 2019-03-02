package ru.nikstep.redink.github

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.GitProperty.BITBUCKET
import ru.nikstep.redink.util.sendRestRequest

class BitbucketWebhookService(pullRequestRepository: PullRequestRepository) :
    AbstractWebhookService(pullRequestRepository) {

    override val JsonObject.gitService: GitProperty
        get() = BITBUCKET

    override val JsonObject.repoId: Long
        get() = -1

    override val JsonObject.number: Int
        get() = obj("pullrequest")!!.int("id")!!

    override val JsonObject.repoFullName: String
        get() = obj("pullrequest")!!.obj("destination")!!.obj("repository")!!.string("full_name")!!

    override val JsonObject.creatorName: String
        get() = obj("pullrequest")!!.obj("author")!!.string("username")!!

    override val JsonObject.headSha: String
        get() = obj("pullrequest")!!.obj("source")!!.obj("commit")!!.string("hash")!!

    override val JsonObject.branchName: String
        get() = obj("pullrequest")!!.obj("source")!!.obj("branch")!!.string("name")!!

    override val JsonObject.changedFiles: List<String>
        get() = sendRestRequest<JsonArray<*>>(
            url = "https://api.bitbucket.org/1.0/repositories/$repoFullName/changesets/$headSha/diffstat"
        ).map { (it as JsonObject).string("file")!! }

}
