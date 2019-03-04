package ru.nikstep.redink.analysis.analyser

import it.zielke.moji.SocketClient
import mu.KotlinLogging
import ru.nikstep.redink.analysis.PreparedAnalysisFiles

/**
 * Client of the Moss plagiarism analysis service.
 * See http://moss.stanford.edu
 */
internal class MossClient(analysisFiles: PreparedAnalysisFiles, private val mossId: String) {
    private val logger = KotlinLogging.logger {}

    private val language = analysisFiles.language.ofMoss()
    private val base = analysisFiles.base
    private val solutions = analysisFiles.solutions

    fun run(): String =
        logged {
            SocketClient().use { client ->
                client.userID = mossId
                client.language = language
                client.run()
                client.uploadFile(base, true)
                solutions.forEach(client::uploadFile)
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