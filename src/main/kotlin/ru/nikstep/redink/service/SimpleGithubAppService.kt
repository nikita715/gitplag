package ru.nikstep.redink.service

import ru.nikstep.redink.util.RequestUtil
import java.io.File

class SimpleGithubAppService : GithubAppService {
    private val bearer = "Bearer "
    private val keygenPath = "src/main/resources/keygen.rb"

    override fun getAccessToken(installationId: Int): String {
        return bearer + RequestUtil.sendAccessTokenRequest(installationId, getToken()).getString("token")
    }

    override fun getAccessTokenHeader(installationId: Int): Pair<String, String> {
        return Pair("Authorization", getAccessToken(installationId))
    }

    private fun getToken(): String {
        val file = File(keygenPath).absolutePath
        val process = Runtime.getRuntime().exec("ruby $file")
        process.waitFor()
        val token = bearer + process.inputStream.bufferedReader().readText().replace("\n", "")
        return token
    }

}