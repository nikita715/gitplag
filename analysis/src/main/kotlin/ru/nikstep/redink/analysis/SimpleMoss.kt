package ru.nikstep.redink.analysis

import it.zielke.moji.SocketClient
import mu.KotlinLogging
import java.io.File

class SimpleMoss(
    override val userId: String,
    override val language: String,
    private val client: SocketClient,
    private val bases: List<File> = emptyList(),
    private val solutions: List<File> = emptyList()
) : Moss {

    private val logger = KotlinLogging.logger {}

    init {
        client.userID = userId
        client.language = language
    }

    override fun base(bases: List<File>): Moss =
        SimpleMoss(userId, language, client, bases, solutions)

    override fun solutions(solutions: List<File>): Moss =
        SimpleMoss(userId, language, client, bases, solutions)

    override fun analyse(): String? {

        if (solutions.isEmpty()) {
            return null
        }

        try {
            client.run()

            bases.forEach { loadBaseFile(it) }
            solutions.forEach { loadFile(it) }

            client.sendQuery()
        } finally {
            client.close()
        }

        return client.resultURL.toString()
    }

    private fun loadBaseFile(file: File): Unit = loadFile(file, isBase = true)

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

