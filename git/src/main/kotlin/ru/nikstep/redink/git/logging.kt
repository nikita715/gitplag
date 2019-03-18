package ru.nikstep.redink.git

import mu.KLogger
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.User

internal fun KLogger.inProgressStatus(pullRequest: PullRequest) {
    this.info {
        pullRequest.run {
            "Webhook: PullRequest: sent in progress status to repo ${repo.name}, user $creatorName," +
                    " branch $sourceBranchName, url https://github.com/${repo.name}/pull/$number"
        }
    }
}

internal fun KLogger.newPullRequest(pullRequest: PullRequest) {
    this.info {
        pullRequest.run {
            "Webhook: PullRequest: new from repo ${repo.name}, user $creatorName," +
                    " branch $sourceBranchName, $gitService"
        }
    }
}

internal fun KLogger.newUser(user: User) {
    this.info { user.run { "Webhook: Integration: registered new user $name" } }
}