package ru.nikstep.redink.github

import com.beust.klaxon.JsonObject
import mu.KotlinLogging
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.repo.PullRequestRepository
import ru.nikstep.redink.util.parseAsObject

abstract class AbstractWebhookService(private val pullRequestRepository: PullRequestRepository) : WebhookService {
    private val logger = KotlinLogging.logger {}

    override fun saveNewPullRequest(payload: String) {
        payload.parseAsObject().let(jsonToPullRequest)
            .let(pullRequestRepository::save)
            .apply(logger::newPullRequest)
    }

    abstract val jsonToPullRequest: (JsonObject) -> PullRequest

}