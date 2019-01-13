package ru.nikstep.redink.core.rest

import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.github.service.IntegrationService
import ru.nikstep.redink.github.service.PullRequestWebhookService
import javax.servlet.http.HttpServletRequest

@RestController
class WebhookController(
    private val pullRequestWebhookService: PullRequestWebhookService,
    private val integrationService: IntegrationService
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping(name = "/webhook", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun processWebhookRequest(@RequestBody payload: String, httpServletRequest: HttpServletRequest) {
        val githubEvent = httpServletRequest.getHeader("X-GitHub-Event")
        logger.info { "Webhook: got new $githubEvent" }
        when (githubEvent) {
            "pull_request" -> pullRequestWebhookService.processPullRequest(payload)
            "integration_installation" -> integrationService.createNewUser(payload)
            else -> logger.info { "Webhook: $githubEvent is not supported" }
        }
    }
}
