package ru.nikstep.redink.rest

import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.service.PullRequestWebhookService
import javax.servlet.http.HttpServletRequest

@RestController
class PullRequestController(
    private val pullRequestWebhookService: PullRequestWebhookService
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping(name = "/webhook", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun processWebhookRequest(@RequestBody payload: String, httpServletRequest: HttpServletRequest) {
        val githubEvent = httpServletRequest.getHeader("X-GitHub-Event")
        logger.info { "Webhook: got new $githubEvent" }
        when (githubEvent) {
            "pull_request" -> {
                pullRequestWebhookService.processPullRequest(payload)
            }
            else -> {
                logger.info { "Webhook: $githubEvent is not supported" }
            }
        }
    }
}
