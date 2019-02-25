package ru.nikstep.redink.core.rest

import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.github.GithubPullRequestWebhookService
import ru.nikstep.redink.github.IntegrationService
import javax.servlet.http.HttpServletRequest

@RestController
class GithubWebhookController(
    private val githubPullRequestWebhookService: GithubPullRequestWebhookService,
    private val integrationService: IntegrationService
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/webhook/github", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun processGithubWebhookRequest(@RequestBody payload: String, httpServletRequest: HttpServletRequest) {
        val event = httpServletRequest.getHeader("X-GitHub-Event")
        logger.info { "Webhook: got new $event" }
        when (event) {
            "pull_request" -> githubPullRequestWebhookService.saveNewPullRequest(payload)
            "integration_installation" -> integrationService.createNewUser(payload)
            else -> logger.info { "Webhook: $event is not supported" }
        }
    }
}
