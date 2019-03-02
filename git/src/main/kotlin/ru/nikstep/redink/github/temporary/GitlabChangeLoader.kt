package ru.nikstep.redink.github.temporary

import com.beust.klaxon.JsonObject
import ru.nikstep.redink.util.sendRestRequest

class GitlabChangeLoader : ChangeLoader {
    override fun loadChanges(
        repoId: Long,
        repoFullName: String,
        number: Int,
        headSha: String,
        secretKey: String
    ): List<String> = sendRestRequest<JsonObject>(
        "https://gitlab.com/api/v4/projects/$repoId/merge_requests/$number/changes"
    ).array<JsonObject>("changes")!!.map { change ->
        change.string("new_path")!!
    }
}