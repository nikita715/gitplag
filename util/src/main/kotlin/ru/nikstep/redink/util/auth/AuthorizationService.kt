package ru.nikstep.redink.util.auth

/**
 * Git authorisation service
 */
interface AuthorizationService {

    /**
     * Get auth token from git service
     */
    fun getAuthorizationToken(installationId: String): String
}