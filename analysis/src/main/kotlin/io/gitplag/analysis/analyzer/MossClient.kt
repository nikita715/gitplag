package io.gitplag.analysis.analyzer

import io.gitplag.model.data.PreparedAnalysisData
import mu.KotlinLogging
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
    private val parameters = analysisData.analysisParameters

    @Synchronized
    fun run(): String {
        val command = "perl $mossPath -l $language $parameters ${if (bases.isNotEmpty()) "-b" else ""}" +
                " ${bases.joinToString(separator = " -b ") { it.absolutePath }} " +
                " ${solutions.joinToString(" ") { it.file.absolutePath }}"
        logger.info { command }
        val exec = Runtime.getRuntime().exec(command)
        exec.waitFor(10, TimeUnit.MINUTES)
        return BufferedReader(InputStreamReader(exec.inputStream)).readLines().last()
    }

}