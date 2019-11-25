package io.gitplag.core.webhook

import io.gitplag.git.payload.BitbucketManager
import io.kotlintest.mock.mock

class BitbucketWebhookControllerTest : AbstractWebhookControllerTest() {
    override val gitManager: BitbucketManager = mock<BitbucketManager>()
    override val webhookController = BitbucketWebhookController(gitManager)
    override val endpoint = "/webhook/bitbucket"
    override val headerName = "X-Event-Key"
    override val pushEvent = "repo"
    override val prEvent = "pullrequest"
}