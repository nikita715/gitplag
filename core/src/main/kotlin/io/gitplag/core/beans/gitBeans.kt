package io.gitplag.core.beans

import io.gitplag.core.util.safeEnvVar
import io.gitplag.git.agent.BitbucketAgent
import io.gitplag.git.agent.GitAgent
import io.gitplag.git.agent.GithubAgent
import io.gitplag.git.agent.GitlabAgent
import io.gitplag.git.payload.BitbucketPayloadProcessor
import io.gitplag.git.payload.GithubPayloadProcessor
import io.gitplag.git.payload.GitlabPayloadProcessor
import io.gitplag.git.payload.PayloadProcessor
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
    bean { GithubAgent(ref(), ref(), env.safeEnvVar("gitplag.githubToken")) }
    bean { GitlabAgent(ref(), ref(), env.safeEnvVar("gitplag.gitlabToken")) }
    bean { BitbucketAgent(ref(), ref(), env.safeEnvVar("gitplag.bitbucketToken")) }
    bean<Map<GitProperty, GitAgent>>("gitRestManagers") {
        mapOf(
            GitProperty.GITHUB to ref<GithubAgent>(),
            GitProperty.BITBUCKET to ref<BitbucketAgent>(),
            GitProperty.GITLAB to ref<GitlabAgent>()
        )
    }
}