package ru.nikstep.redink.github.temporary

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import ru.nikstep.redink.util.sendRestRequest

class BitbucketChangeLoader : ChangeLoader {
    override fun loadChanges(
        repoId: Long,
        repoFullName: String,
        number: Int,
        headSha: String,
        secretKey: String
    ): List<String> =
        sendRestRequest<JsonArray<*>>(
            url = "https://api.bitbucket.org/1.0/repositories/$repoFullName/changesets/$headSha/diffstat"
        ).map { (it as JsonObject).string("file")!! }
}