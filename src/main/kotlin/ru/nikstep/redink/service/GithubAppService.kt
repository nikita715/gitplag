package ru.nikstep.redink.service

import ru.nikstep.redink.entity.User

interface GithubAppService {
    fun getToken(): String
    fun getAccessToken(installationId: Int): String
    fun getAccessToken(user: User): String
}