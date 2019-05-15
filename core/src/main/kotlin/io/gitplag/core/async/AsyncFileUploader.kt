package io.gitplag.core.async

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.HttpException
import io.gitplag.core.websocket.NotificationService
import io.gitplag.git.payload.PayloadProcessor
import io.gitplag.git.rest.GitRestManager
import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.GitProperty
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

/**
 * The class that asynchronously initiates the downloading of files from git
 */
@Component
class AsyncFileUploader(
    @Qualifier("gitRestManagers") private val restManagers: Map<GitProperty, GitRestManager>,
    @Qualifier("payloadProcessors") private val payloadProcessors: Map<GitProperty, PayloadProcessor>,
    private val notificationService: NotificationService
) {

    /**
     * Download files of the [repository] from git
     */
    @Async("customExecutor")
    fun uploadFiles(repository: Repository) {
        val payloadProcessor = payloadProcessors.getValue(repository.gitService)
        notificationService.notify("Started upload of files from repo ${repository.name}.")
        try {
            payloadProcessor.downloadAllPullRequestsOfRepository(repository)
        } catch (e: Exception) {
            var message = "Failed upload of files from repo ${repository.name}."
            when (e) {
                is FuelError,
                is HttpException -> {
                    message += " Access to git is denied."
                }
            }
            notificationService.notify(message)
            throw e
        }
        notificationService.notify("Ended upload of files from repo ${repository.name}")
    }
}
