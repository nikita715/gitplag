package ru.nikstep.redink.github.webhook

interface WebhookService {

    fun saveNewPullRequest(payload: String)

}