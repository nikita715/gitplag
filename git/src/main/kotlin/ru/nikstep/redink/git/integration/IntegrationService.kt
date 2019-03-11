package ru.nikstep.redink.git.integration

/**
 * Service that receives webhook requests about user integration from git services
 */
interface IntegrationService {

    /**
     * Transform payload from a git service
     * and save it as a new user
     */
    fun createNewUser(payload: String)

    /**
     * Create and delete repositories mentioned in the [payload]
     */
    fun manageRepositories(payload: String)
}