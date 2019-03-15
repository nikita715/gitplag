package ru.nikstep.redink.analysis.analyser

import it.zielke.moji.SocketClient
import mu.KotlinLogging
import org.apache.commons.lang3.reflect.FieldUtils
import ru.nikstep.redink.model.data.PreparedAnalysisData

/**
 * Client of the Moss plagiarism analysis service.
 * See http://moss.stanford.edu
 */
internal class MossClient(analysisData: PreparedAnalysisData, private val mossId: String) {
    private val logger = KotlinLogging.logger {}

    private val language = analysisData.language.ofMoss()
    private val bases = analysisData.bases
    private val solutions = analysisData.solutions

    @Synchronized
    fun run(): String =
        logged {
            SocketClient().use { client ->
                client.userID = mossId
                client.language = language
//                client.optD(0)
                client.run()
                bases.forEach { client.uploadFile(it, true) }
                solutions.forEach { client.uploadFile(it.file) }
                client.sendQuery()
                client.resultURL.toString()
            }
        }

    fun SocketClient.optD(value: Int) {
        this::class.java.getDeclaredField("optD").isAccessible = true
        FieldUtils.writeField(this, "optD", value, true)
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