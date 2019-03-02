package ru.nikstep.redink.git

import mu.KLogger
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.User

internal fun KLogger.inProgressStatus(pullRequest: PullRequest) {
    this.info {
        pullRequest.run {
            "Webhook: PullRequest: sent in progress status to repo $repoFullName, user $creatorName," +
                    " branch $branchName, url https://github.com/$repoFullName/pull/$number"
        }
    }
}

internal fun KLogger.newPullRequest(pullRequest: PullRequest) {
    this.info {
        pullRequest.run {
            "Webhook: PullRequest: new from repo $repoFullName, user $creatorName," +
                    " branch $branchName, $gitService"
        }
    }
}

internal fun KLogger.newUser(user: User) {
    this.info { user.run { "Webhook: Integration: registered new user $name" } }
}