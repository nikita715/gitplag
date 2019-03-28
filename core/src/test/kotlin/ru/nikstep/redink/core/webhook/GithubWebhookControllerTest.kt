package ru.nikstep.redink.core.webhook

import io.kotlintest.mock.mock
import ru.nikstep.redink.git.webhook.GithubPayloadProcessor

class GithubWebhookControllerTest : AbstractWebhookControllerTest() {
    override val payloadProcessor: GithubPayloadProcessor = mock<GithubPayloadProcessor>()
    override val webhookController: GithubWebhookController = GithubWebhookController(payloadProcessor)
    override val endpoint: String = "/webhook/github"
    override val headerName: String = "X-GitHub-Event"
    override val pushEvent: String = "push"
    override val prEvent: String = "pull_request"
}