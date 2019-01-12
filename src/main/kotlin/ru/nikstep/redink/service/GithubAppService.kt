package ru.nikstep.redink.service

interface GithubAppService {
    fun getAccessToken(installationId: Int): String
    fun getAccessTokenHeader(installationId: Int): Pair<String, String>
}