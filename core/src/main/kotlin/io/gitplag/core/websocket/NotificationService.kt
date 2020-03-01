package io.gitplag.core.websocket

import org.apache.commons.lang3.StringUtils
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
        if (StringUtils.isNotEmpty(notification)) {
            messagingTemplate.convertAndSend(
                "/queue/notify",
                notification ?: ""
            )
        }
    }
}