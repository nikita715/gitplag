package ru.nikstep.redink.model

import org.springframework.context.ApplicationEvent
import ru.nikstep.redink.model.entity.PullRequest

class PullRequestEvent(source: Any, val pullRequest: PullRequest) : ApplicationEvent(source)