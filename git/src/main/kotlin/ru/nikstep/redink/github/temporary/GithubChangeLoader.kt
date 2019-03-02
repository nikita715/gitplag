package ru.nikstep.redink.github.temporary

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import ru.nikstep.redink.util.auth.AuthorizationService
import ru.nikstep.redink.util.sendRestRequest

class GithubChangeLoader(
    private val authorizationService: AuthorizationService
) : ChangeLoader {
    override fun loadChanges(
        repoId: Long,
        repoFullName: String,
        number: Int,
        headSha: String,
        secretKey: String
    ): List<String> {
        return sendRestRequest<JsonArray<*>>(
            url = "https://api.github.com/repos/$repoFullName/pulls/$number/files",
            accessToken = authorizationService.getAuthorizationToken(secretKey)
        ).map { (it as JsonObject).string("filename")!! }
    }
}