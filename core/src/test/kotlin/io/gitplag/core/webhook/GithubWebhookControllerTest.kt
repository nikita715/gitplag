package io.gitplag.core.webhook

import io.gitplag.git.payload.GithubManager
import io.kotlintest.mock.mock

class GithubWebhookControllerTest : AbstractWebhookControllerTest() {
    override val gitManager: GithubManager = mock<GithubManager>()
    override val webhookController = GithubWebhookController(gitManager)
    override val endpoint: String = "/webhook/github"
    override val headerName: String = "X-GitHub-Event"
    override val pushEvent: String = "push"
    override val prEvent: String = "pull_request"
}