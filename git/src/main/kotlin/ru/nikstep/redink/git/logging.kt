package ru.nikstep.redink.git

import mu.KLogger
import ru.nikstep.redink.model.entity.PullRequest

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
                    " branch $sourceBranchName, ${repo.gitService}"
        }
    }
}
