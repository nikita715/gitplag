package ru.nikstep.redink.util.auth

import mu.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.util.ResourceUtils
import ru.nikstep.redink.util.RequestUtil.Companion.sendAccessTokenRequest

open class GithubAuthorizationService : AuthorizationService {

    private val logger = KotlinLogging.logger {}

    private val bearer = "Bearer "

    @Cacheable(cacheNames = ["githubAccessTokens"])
    override fun getAuthorizationToken(installationId: Int): String {
        logger.info { "Authorization: new request for access token from github" }
        return bearer + sendAccessTokenRequest(installationId, getToken()).getString("token")
    }

    private fun getToken(): String {
        val file = ResourceUtils.getFile("classpath:keygen.rb")
        val process = Runtime.getRuntime().exec("ruby $file")
        process.waitFor()
        val token = bearer + process.inputStream.bufferedReader().readText().replace("\n", "")
        return token
    }
}