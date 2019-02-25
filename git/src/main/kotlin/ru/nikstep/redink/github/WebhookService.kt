package ru.nikstep.redink.github

interface WebhookService {

    fun saveNewPullRequest(payload: String)

}