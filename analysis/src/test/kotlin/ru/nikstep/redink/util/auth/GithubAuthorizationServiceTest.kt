package ru.nikstep.redink.util.auth

import org.junit.Test
import kotlin.test.assertTrue

class GithubAuthorizationServiceTest {

    private val githubAuthorizationService = GithubAuthorizationService()

    @Test
    fun getAuthorizationToken() {
        val token = githubAuthorizationService.getAuthorizationToken(447213)
        println(token)
        assertTrue(token.run { length > 0 })
    }
}