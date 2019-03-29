package ru.nikstep.redink.analysis.analyser

import mu.KotlinLogging
import ru.nikstep.redink.model.data.PreparedAnalysisData
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

/**
 * Client of the Moss plagiarism analysis service.
 * See http://moss.stanford.edu
 */
internal class MossClient(analysisData: PreparedAnalysisData, private val mossPath: String) {
    private val language = analysisData.language.ofMoss()
    private val bases = analysisData.bases
    private val solutions = analysisData.solutions
    private val logger = KotlinLogging.logger {}

    @Synchronized
    fun run(): String {
        val command = "perl $mossPath -d -l $language ${if (bases.isNotEmpty()) "-b" else ""}" +
                " ${bases.joinToString(separator = " -b ") { it.absolutePath }} " +
                " ${solutions.joinToString(" ") { it.file.absolutePath }}"
        val exec = Runtime.getRuntime().exec(command)
        exec.waitFor(10, TimeUnit.MINUTES)
        val reader = BufferedReader(InputStreamReader(exec.inputStream))
        logger.info { command }
        return reader.readLines().last()
    }

}