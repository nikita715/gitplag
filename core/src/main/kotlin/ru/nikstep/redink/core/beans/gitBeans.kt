package ru.nikstep.redink.core.beans

import org.springframework.context.support.beans
import ru.nikstep.redink.git.webhook.BitbucketPayloadProcessor
import ru.nikstep.redink.git.webhook.GithubPayloadProcessor
import ru.nikstep.redink.git.webhook.GitlabPayloadProcessor
import ru.nikstep.redink.git.webhook.PayloadProcessor
import ru.nikstep.redink.util.GitProperty

val gitBeans = beans {
    bean<GitlabPayloadProcessor>()
    bean<BitbucketPayloadProcessor>()
    bean<GithubPayloadProcessor>()
    bean<Map<GitProperty, PayloadProcessor>>("payloadProcessors") {
        mapOf(
            GitProperty.GITHUB to ref<GithubPayloadProcessor>(),
            GitProperty.GITLAB to ref<GitlabPayloadProcessor>(),
            GitProperty.BITBUCKET to ref<BitbucketPayloadProcessor>()
        )
    }
}