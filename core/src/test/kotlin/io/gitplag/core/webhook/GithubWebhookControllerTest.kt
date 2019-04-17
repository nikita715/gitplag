package io.gitplag.core.webhook

import io.gitplag.git.payload.GithubPayloadProcessor
import io.kotlintest.mock.mock

class GithubWebhookControllerTest : AbstractWebhookControllerTest() {
    override val payloadProcessor: GithubPayloadProcessor = mock<GithubPayloadProcessor>()
    override val webhookController = GithubWebhookController(payloadProcessor)
    override val endpoint: String = "/webhook/github"
    override val headerName: String = "X-GitHub-Event"
    override val pushEvent: String = "push"
    override val prEvent: String = "pull_request"
}