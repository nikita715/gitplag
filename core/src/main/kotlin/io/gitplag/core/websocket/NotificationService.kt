package io.gitplag.core.websocket

import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

/**
 * Service class for sending notification messages.
 */
@Service
class NotificationService(private val messagingTemplate: SimpMessagingTemplate) {

    /**
     * Send notification to users subscribed on channel "/queue/notify".
     */
    fun notify(notification: String?) {
        messagingTemplate.convertAndSend(
            "/queue/notify",
            notification ?: ""
        )
    }
}