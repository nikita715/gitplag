package ru.nikstep.redink.github.service

import org.springframework.util.ResourceUtils
import ru.nikstep.redink.github.util.RequestUtil

class SimpleGithubAppService : GithubAppService {
    private val bearer = "Bearer "

    override fun getAccessToken(installationId: Int): String {
        return bearer + RequestUtil.sendAccessTokenRequest(installationId, getToken()).getString("token")
    }

    override fun getAccessTokenHeader(installationId: Int): Pair<String, String> {
        return Pair("Authorization", getAccessToken(installationId))
    }

    private fun getToken(): String {
        val file = ResourceUtils.getFile("classpath:keygen.rb")
        val process = Runtime.getRuntime().exec("ruby $file")
        process.waitFor()
        val token = bearer + process.inputStream.bufferedReader().readText().replace("\n", "")
        return token
    }

}