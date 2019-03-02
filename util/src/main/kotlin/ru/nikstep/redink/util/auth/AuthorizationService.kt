package ru.nikstep.redink.util.auth

interface AuthorizationService {
    fun getAuthorizationToken(installationId: String): String
}