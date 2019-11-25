package io.gitplag.core.webhook

import io.gitplag.git.payload.GitlabManager
import io.kotlintest.mock.mock

class GitlabWebhookControllerTest : AbstractWebhookControllerTest() {
    override val gitManager: GitlabManager = mock<GitlabManager>()
    override val webhookController = GitlabWebhookController(gitManager)
    override val endpoint: String = "/webhook/gitlab"
    override val headerName: String = "X-Gitlab-Event"
    override val pushEvent: String = "Push Hook"
    override val prEvent: String = "Merge Request Hook"
}