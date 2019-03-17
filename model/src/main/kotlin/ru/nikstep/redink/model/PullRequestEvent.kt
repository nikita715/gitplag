package ru.nikstep.redink.model

import org.springframework.context.ApplicationEvent
import ru.nikstep.redink.model.entity.PullRequest

/**
 * Event about a new [PullRequest]
 */
data class PullRequestEvent(
    @get:JvmName("getSource_")
    val source: Any,
    val pullRequest: PullRequest
) : ApplicationEvent(source)