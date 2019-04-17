package io.gitplag.core.beans

import io.gitplag.git.payload.BitbucketPayloadProcessor
import io.gitplag.git.payload.GithubPayloadProcessor
import io.gitplag.git.payload.GitlabPayloadProcessor
import io.gitplag.git.payload.PayloadProcessor
import io.gitplag.git.rest.BitbucketRestManager
import io.gitplag.git.rest.GitRestManager
import io.gitplag.git.rest.GithubRestManager
import io.gitplag.git.rest.GitlabRestManager
import io.gitplag.model.enums.GitProperty
import org.springframework.context.support.beans

val gitBeans = beans {

    // Payload processors
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

    // Loaders
    bean<GithubRestManager>()
    bean<BitbucketRestManager>()
    bean<GitlabRestManager>()
    bean<Map<GitProperty, GitRestManager>>("gitRestManagers") {
        mapOf(
            GitProperty.GITHUB to ref<GithubRestManager>(),
            GitProperty.BITBUCKET to ref<BitbucketRestManager>(),
            GitProperty.GITLAB to ref<GitlabRestManager>()
        )
    }
}