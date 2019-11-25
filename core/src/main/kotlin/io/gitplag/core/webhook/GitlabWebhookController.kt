package io.gitplag.core.webhook

import io.gitplag.git.payload.GitlabManager
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

/**
 * Receiver of gitlab webhook messages
 */
@RestController
class GitlabWebhookController(private val gitlabWebhookService: GitlabManager) {
    private val logger = KotlinLogging.logger {}

    /**
     * Receive and process [payload] from gitlab
     */
    @PostMapping("/webhook/gitlab", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun processGithubWebhookRequest(@RequestBody payload: String, httpServletRequest: HttpServletRequest) {
        val event = httpServletRequest.getHeader("X-Gitlab-Event")
        logger.info { "Webhook: got new $event" }
        when (event) {
            "Merge Request Hook" -> gitlabWebhookService.downloadSolutionsOfPullRequest(payload)
            "Push Hook" -> gitlabWebhookService.downloadBasesOfRepository(payload)
            else -> logger.info { "Webhook: $event is not supported" }
        }
    }
}
