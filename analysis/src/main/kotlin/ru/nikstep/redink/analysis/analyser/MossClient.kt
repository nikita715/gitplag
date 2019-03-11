package ru.nikstep.redink.analysis.analyser

import it.zielke.moji.SocketClient
import mu.KotlinLogging
import ru.nikstep.redink.analysis.data.PreparedAnalysisFiles

/**
 * Client of the Moss plagiarism analysis service.
 * See http://moss.stanford.edu
 */
internal class MossClient(analysisFiles: PreparedAnalysisFiles, private val mossId: String) {
    private val logger = KotlinLogging.logger {}

    private val language = analysisFiles.language.ofMoss()
    private val bases = analysisFiles.bases
    private val solutions = analysisFiles.solutions

    @Synchronized
    fun run(): String =
        logged {
            SocketClient().use { client ->
                client.userID = mossId
                client.language = language
                client.run()
                bases.forEach { client.uploadFile(it, true) }
                solutions.values.forEach { client.uploadFile(it.file) }
                client.sendQuery()
                client.resultURL.toString()
            }
        }

    private inline fun <R> logged(action: () -> R): R {
        logger.info { "Analysis: starting new moss analysis" }
        val result = action()
        logger.info { "Analysis: performed new moss analysis at $result" }
        return result
    }

    private inline fun <R> SocketClient.use(action: (SocketClient) -> R): R =
        try {
            action(this)
        } finally {
            close()
        }

}