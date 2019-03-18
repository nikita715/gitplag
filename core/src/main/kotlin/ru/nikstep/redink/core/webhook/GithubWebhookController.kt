package ru.nikstep.redink.core.webhook

import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.git.webhook.GithubWebhookService
import javax.servlet.http.HttpServletRequest

/**
 * Receiver of github webhook messages
 */
@RestController
class GithubWebhookController(
    private val githubWebhookService: GithubWebhookService
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Receive and process [payload] from github
     */
    @PostMapping("/webhook/github", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun processGithubWebhookRequest(@RequestBody payload: String, httpServletRequest: HttpServletRequest) {
        val event = httpServletRequest.getHeader("X-GitHub-Event")
        logger.info { "Webhook: got new $event" }
        when (event) {
            "pull_request" -> githubWebhookService.saveNewPullRequest(payload)
            "push" -> githubWebhookService.saveNewBaseFiles(payload)
            else -> logger.info { "Webhook: $event is not supported" }
        }
    }
}
