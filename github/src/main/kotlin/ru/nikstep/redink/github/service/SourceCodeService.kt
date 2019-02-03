package ru.nikstep.redink.github.service

import ru.nikstep.redink.data.PullRequestData
import java.io.File

interface SourceCodeService {
    fun save(prData: PullRequestData, fileName: String, fileText: String)
    fun load(userId: Long, repoId: Long, fileName: String): File
    fun load(repoId: Long, fileName: String): List<File>
}