package io.gitplag.core.async

import io.gitplag.core.websocket.NotificationService
import io.gitplag.git.payload.PayloadProcessor
import io.gitplag.git.rest.GitRestManager
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.GitProperty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class AsyncFileUploader(
    @Qualifier("gitRestManagers") private val restManagers: Map<GitProperty, GitRestManager>,
    @Qualifier("payloadProcessors") private val payloadProcessors: Map<GitProperty, PayloadProcessor>,
    private val notificationService: NotificationService
) {

    @Async("customExecutor")
    fun uploadFiles(repository: Repository) {
        val gitRestManager = restManagers.getValue(repository.gitService)
        val payloadProcessor = payloadProcessors.getValue(repository.gitService)
        notificationService.notify("Started upload of files from repo ${repository.name}")
        gitRestManager.cloneRepository(repository)
        payloadProcessor.downloadAllPullRequestsOfRepository(repository)
        notificationService.notify("Ended upload of files from repo ${repository.name}")
    }

}