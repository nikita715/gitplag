package ru.nikstep.redink.core.webhook

import io.kotlintest.mock.mock
import ru.nikstep.redink.git.webhook.BitbucketPayloadProcessor

class BitbucketWebhookControllerTest : AbstractWebhookControllerTest() {
    override val payloadProcessor: BitbucketPayloadProcessor = mock<BitbucketPayloadProcessor>()
    override val webhookController = BitbucketWebhookController(payloadProcessor)
    override val endpoint = "/webhook/bitbucket"
    override val headerName = "X-Event-Key"
    override val pushEvent = "repo"
    override val prEvent = "pullrequest"
}