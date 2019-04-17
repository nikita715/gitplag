package io.gitplag.core.webhook

import io.gitplag.git.payload.BitbucketPayloadProcessor
import io.kotlintest.mock.mock

class BitbucketWebhookControllerTest : AbstractWebhookControllerTest() {
    override val payloadProcessor: BitbucketPayloadProcessor = mock<BitbucketPayloadProcessor>()
    override val webhookController = BitbucketWebhookController(payloadProcessor)
    override val endpoint = "/webhook/bitbucket"
    override val headerName = "X-Event-Key"
    override val pushEvent = "repo"
    override val prEvent = "pullrequest"
}