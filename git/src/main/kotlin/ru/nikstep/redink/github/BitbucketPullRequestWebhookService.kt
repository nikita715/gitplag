package ru.nikstep.redink.github

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.Git.BITBUCKET
import ru.nikstep.redink.util.JsonArrayDeserializer
import ru.nikstep.redink.util.parseAsObject
import ru.nikstep.redink.util.sendRestRequest

class BitbucketPullRequestWebhookService(private val pullRequestRepository: PullRequestRepository) : WebhookService {

    private val logger = KotlinLogging.logger {}

    override fun saveNewPullRequest(payload: String) {
        payload.parseAsObject().let { jsonPayload ->

            val pullRequestJson = jsonPayload.obj("pullrequest")!!

            val headSha = pullRequestJson.obj("source")!!.obj("commit")!!.string("hash")!!
            val repoFullName = pullRequestJson.obj("destination")!!.obj("repository")!!.string("full_name")!!

            val jsonChangedFiles = sendRestRequest(
                url = "https://api.bitbucket.org/1.0/repositories/$repoFullName/changesets/$headSha/diffstat",
                deserializer = JsonArrayDeserializer()
            ) as JsonArray<*>

            PullRequest(
                gitService = BITBUCKET,
                repoId = -1,
                number = pullRequestJson.int("id")!!,
                repoFullName = repoFullName,
                creatorName = pullRequestJson.obj("author")!!.string("username")!!,
                headSha = headSha,
                branchName = pullRequestJson.obj("source")!!.obj("branch")!!.string("name")!!,
                changedFiles = jsonChangedFiles.map { (it as JsonObject).string("file")!! }
            )
        }.let(pullRequestRepository::save)
            .apply(logger::newPullRequest)
    }

}

