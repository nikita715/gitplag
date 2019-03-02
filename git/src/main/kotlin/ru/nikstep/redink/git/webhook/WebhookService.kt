package ru.nikstep.redink.git.webhook

interface WebhookService {

    fun saveNewPullRequest(payload: String)

}