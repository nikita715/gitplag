package ru.nikstep.redink.service

import com.github.kittinunf.fuel.httpPost
import ru.nikstep.redink.entity.User
import java.io.File

class SimpleGithubAppService : GithubAppService {
    override fun getAccessToken(installationId: Int): String {
        return "Bearer " + "https://api.github.com/app/installations/$installationId/access_tokens".httpPost()
            .header(
                "Authorization" to getToken(),
                "Accept" to "application/vnd.github.machine-man-preview+json"
            )
            .responseObject(PullRequestSavingService.JsonObjectDeserializer()).third.get().getString("token")
    }

    override fun getAccessToken(user: User): String {
        TODO("not implemented")
    }

    override fun getToken(): String {
        val file = File("src/main/resources/keygen.rb").absolutePath
        val process = Runtime.getRuntime().exec("ruby $file")
        process.waitFor()
        val token = "Bearer " + process.inputStream.bufferedReader().readText().replace("\n", "")
        return token
    }

}