package ru.nikstep.redink.analysis

import it.zielke.moji.SocketClient
import mu.KotlinLogging
import java.io.File

class MossClient(
    val userId: String,
    val language: String,
    private val client: SocketClient,
    private val base: File,
    private val solutions: List<File> = emptyList()
) : AnalysisSystemClient {

    private val logger = KotlinLogging.logger {}

    override fun base(base: File): AnalysisSystemClient =
        MossClient(userId, language, client, base, solutions)

    override fun solutions(solutions: List<File>): AnalysisSystemClient =
        MossClient(userId, language, client, base, solutions)

    @Synchronized
    override fun analyse(): String? {

        Thread.sleep(1000)

        client.userID = userId
        client.language = language

        if (solutions.isEmpty()) {
            return null
        }

        try {
            client.run()

            loadBaseFile(base)
            solutions.forEach { loadFile(it) }

            client.sendQuery()
        } finally {
            client.close()
        }

        return client.resultURL.toString()
    }

    private fun loadBaseFile(file: File) = loadFile(file, isBase = true)

    private fun loadFile(file: File, isBase: Boolean = false) =
        try {
            client.uploadFile(file, isBase)
        } catch (e: Exception) {
            logger.error(
                "Can't load ${if (isBase) "base" else "solutions"} " +
                        "file ${file.name} to moss server", e
            )
        }

}

