package ru.nikstep.redink.core.rest

import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.nikstep.redink.github.BitbucketPullRequestWebhookService
import javax.servlet.http.HttpServletRequest

@RestController("")
class BitbucketWebhookController(private val bitbucketPullRequestWebhookService: BitbucketPullRequestWebhookService) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/webhook/bitbucket", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun processBitbucketWebhookRequest(@RequestBody payload: String, httpServletRequest: HttpServletRequest) {
        val event = httpServletRequest.getHeader("X-Event-Key")
        logger.info { "Webhook: got new $event" }
        when (event.substringBefore(":")) {
            "pullrequest" -> bitbucketPullRequestWebhookService.saveNewPullRequest(payload)
            else -> logger.info { "Webhook: $event is not supported" }
        }
    }
}