package ru.nikstep.redink.analysis

import mu.KLogger
import ru.nikstep.redink.model.entity.PullRequest


internal fun KLogger.loggedAnalysis(pullRequest: PullRequest, action: () -> Unit) {
    info {
        "Analysis: start analysing of pr #${pullRequest.number}," +
                " repo ${pullRequest.repoFullName}, user ${pullRequest.creatorName}"
    }
    action()
    info {
        "Analysis: complete analysing of pr #${pullRequest.number}," +
                " repo ${pullRequest.repoFullName}, user ${pullRequest.creatorName}"
    }
}