package ru.nikstep.redink.util.auth

import org.springframework.util.ResourceUtils
import ru.nikstep.redink.util.RequestUtil

class GithubAuthorizationService : AuthorizationService {
    private val bearer = "Bearer "

    override fun getAuthorizationToken(installationId: Int): String {
        return bearer + RequestUtil.sendAccessTokenRequest(installationId, getToken()).getString("token")
    }

    private fun getToken(): String {
        val file = ResourceUtils.getFile("classpath:keygen.rb")
        val process = Runtime.getRuntime().exec("ruby $file")
        process.waitFor()
        val token = bearer + process.inputStream.bufferedReader().readText().replace("\n", "")
        return token
    }
}