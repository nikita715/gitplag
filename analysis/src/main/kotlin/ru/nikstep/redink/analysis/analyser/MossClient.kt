package ru.nikstep.redink.analysis.analyser

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

    @Synchronized
    fun run(): String {
        val exec = Runtime.getRuntime().exec(
            "perl $mossPath -l $language -b" +
                    " ${bases.joinToString(separator = " -b ") { it.absolutePath }} " +
                    " ${solutions.joinToString(" ") { it.file.absolutePath }}"
        )
        exec.waitFor(10, TimeUnit.MINUTES)
        return BufferedReader(InputStreamReader(exec.inputStream)).readLines().last()
    }

}