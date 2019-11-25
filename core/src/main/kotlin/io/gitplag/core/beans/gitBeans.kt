package io.gitplag.core.beans

import io.gitplag.core.util.safeEnvVar
import io.gitplag.git.agent.BitbucketAgent
import io.gitplag.git.agent.GitAgent
import io.gitplag.git.agent.GithubAgent
import io.gitplag.git.agent.GitlabAgent
import io.gitplag.git.payload.BitbucketManager
import io.gitplag.git.payload.GitManager
import io.gitplag.git.payload.GithubManager
import io.gitplag.git.payload.GitlabManager
import io.gitplag.model.enums.GitProperty
import org.springframework.context.support.beans

val gitBeans = beans {

    // Payload processors
    bean<GitlabManager>()
    bean<BitbucketManager>()
    bean<GithubManager>()
    bean<Map<GitProperty, GitManager>>("payloadProcessors") {
        mapOf(
            GitProperty.GITHUB to ref<GithubManager>(),
            GitProperty.GITLAB to ref<GitlabManager>(),
            GitProperty.BITBUCKET to ref<BitbucketManager>()
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