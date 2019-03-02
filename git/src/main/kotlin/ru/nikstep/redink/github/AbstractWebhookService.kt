package ru.nikstep.redink.github

import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.parseAsObject

abstract class AbstractWebhookService(private val pullRequestRepository: PullRequestRepository) : WebhookService {
    private val logger = KotlinLogging.logger {}

    override fun saveNewPullRequest(payload: String) {
        payload.parseAsObject()
            .let(jsonToPullRequest)
            .let(pullRequestRepository::save)
            .apply(logger::newPullRequest)
    }

    open val jsonToPullRequest: (JsonObject) -> PullRequest = { jsonObject ->
        jsonObject.run {
            PullRequest(
                gitService = gitService,
                repoId = repoId,
                number = number,
                repoFullName = repoFullName,
                creatorName = creatorName,
                headSha = headSha,
                branchName = branchName,
                secretKey = secretKey,
                changedFiles = changedFiles
            )
        }
    }

    protected abstract val JsonObject.gitService: GitProperty

    protected abstract val JsonObject.repoId: Long

    protected abstract val JsonObject.number: Int

    protected abstract val JsonObject.repoFullName: String

    protected abstract val JsonObject.creatorName: String

    protected abstract val JsonObject.headSha: String

    protected abstract val JsonObject.branchName: String

    protected abstract val JsonObject.changedFiles: List<String>

    protected open val JsonObject.secretKey: String
        get() = ""
}