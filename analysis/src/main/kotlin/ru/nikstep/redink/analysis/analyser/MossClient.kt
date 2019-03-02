package ru.nikstep.redink.analysis.analyser

import it.zielke.moji.SocketClient
import mu.KotlinLogging
import ru.nikstep.redink.analysis.PreparedAnalysisFiles

internal class MossClient(analysisFiles: PreparedAnalysisFiles, private val mossId: String) {
    private val logger = KotlinLogging.logger {}

    val language = analysisFiles.language.ofMoss()
    val base = analysisFiles.base
    val solutions = analysisFiles.solutions

    fun run(): String =
        logged {
            SocketClient().use { client ->
                client.userID = mossId
                client.language = language
                client.run()
                client.uploadFile(base, true)
                solutions.forEach { client.uploadFile(it) }
                client.sendQuery()
                client.resultURL.toString()
            }
        }

    private fun <R> logged(action: () -> R): R {
        logger.info { "Analysis: starting new moss analysis" }
        val result = action()
        logger.info { "Analysis: performed new moss analysis at $result" }
        return result
    }

    private fun <R> SocketClient.use(action: (SocketClient) -> R): R =
        try {
            action(this)
        } finally {
            close()
        }

}