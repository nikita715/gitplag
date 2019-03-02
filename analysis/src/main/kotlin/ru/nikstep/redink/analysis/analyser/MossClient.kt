package ru.nikstep.redink.analysis.analyser

import it.zielke.moji.SocketClient
import mu.KotlinLogging
import ru.nikstep.redink.analysis.AnalysisException
import ru.nikstep.redink.analysis.PreparedAnalysisFiles

internal class MossClient(private val analysisFiles: PreparedAnalysisFiles, private val mossId: String) {
    private val logger = KotlinLogging.logger {}

    fun run(): String =
        if (analysisFiles.solutions.isEmpty())
            throw AnalysisException("Analysis: No solutions for file ${analysisFiles.base.absolutePath}")
        else
            SocketClient().use { client ->
                client.userID = mossId
                client.language = analysisFiles.language.ofMoss()
                run()
                client.uploadFile(analysisFiles.base, true)
                analysisFiles.solutions.forEach { client.uploadFile(it) }
                client.sendQuery()
                client.resultURL.toString().also {
                    logger.info { "Analysis: performed new analysis at $it" }
                }

            }

    private fun <R> SocketClient.use(action: (SocketClient) -> R): R =
        try {
            action(this)
        } finally {
            close()
        }

}