package ru.nikstep.redink.core.webhook

import io.kotlintest.mock.mock
import ru.nikstep.redink.git.webhook.GitlabPayloadProcessor

class GitlabWebhookControllerTest : AbstractWebhookControllerTest() {
    override val payloadProcessor: GitlabPayloadProcessor = mock<GitlabPayloadProcessor>()
    override val webhookController = GitlabWebhookController(payloadProcessor)
    override val endpoint: String = "/webhook/gitlab"
    override val headerName: String = "X-Gitlab-Event"
    override val pushEvent: String = "Push Hook"
    override val prEvent: String = "Merge Request Hook"
}