package ru.nikstep.redink.analysis.analyser

import mu.KotlinLogging
import ru.nikstep.redink.model.data.PreparedAnalysisData
import ru.nikstep.redink.util.asPath
import ru.nikstep.redink.util.asPathInRoot
import java.util.concurrent.TimeUnit

/**
 * Client of the JPlag plagiarism analysis service.
 * See https://jplag.ipd.kit.edu
 */
internal class JPlagClient(
    analysisData: PreparedAnalysisData,
    private val solutionsDir: String,
    private val branchName: String,
    private val resultDir: String
) {

    private val logger = KotlinLogging.logger {}
    private val jplagPath = asPath("libs".asPathInRoot(), "jplag.jar")

    private val language = analysisData.language.ofJPlag()
    private val gitService = analysisData.gitService
    private val repoName = analysisData.repoName

    fun run() =
        buildString {
            append("java -jar $jplagPath  -l $language -bc .base -r $resultDir -s ")
            append(asPath(solutionsDir, gitService, repoName, branchName))
        }.also(::execute)

    private fun execute(task: String) {
        Runtime.getRuntime().exec(task).waitFor(1, TimeUnit.MINUTES)
    }

}