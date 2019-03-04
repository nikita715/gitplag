package ru.nikstep.redink.util.auth

import io.kotlintest.matchers.shouldNotBe
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

class GithubAuthorizationServiceTest {

    private val githubAuthorizationService = GithubAuthorizationService()
    private val installationId = System.getenv("TEST_GITHUB_INST_ID")

    @Before
    fun setUp() {
    }

    @Test
    @Ignore
    fun getAuthorizationToken() {
        githubAuthorizationService.getAuthorizationToken(installationId) shouldNotBe ""
    }
}