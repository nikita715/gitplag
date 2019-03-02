package ru.nikstep.redink.github.temporary

interface ChangeLoader {
    fun loadChanges(repoId: Long, repoFullName: String, number: Int, headSha: String, secretKey: String): List<String>
}