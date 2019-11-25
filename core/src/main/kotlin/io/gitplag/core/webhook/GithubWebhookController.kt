package io.gitplag.core.webhook

import io.gitplag.git.payload.GithubManager
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * Receiver of github webhook messages
 */
@RestController
class GithubWebhookController(private val githubWebhookService: GithubManager) {
    private val logger = KotlinLogging.logger {}

    /**
     * Receive and process [payload] from github
     */
    @PostMapping("/webhook/github", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun processGithubWebhookRequest(@RequestBody payload: String, httpServletRequest: HttpServletRequest) {
        val event = httpServletRequest.getHeader("X-GitHub-Event")
        logger.info { "Webhook: got new $event" }
        when (event) {
            "pull_request" -> githubWebhookService.downloadSolutionsOfPullRequest(payload)
            "push" -> githubWebhookService.downloadBasesOfRepository(payload)
            else -> logger.info { "Webhook: $event is not supported" }
        }
    }
}
