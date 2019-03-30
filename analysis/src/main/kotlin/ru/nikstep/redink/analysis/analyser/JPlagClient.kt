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
    private val resultDir: String
) {

    private val logger = KotlinLogging.logger {}
    private val jplagPath = asPath("libs".asPathInRoot(), "jplag.jar")

    private val language = analysisData.language.ofJPlag()
    private val baseCount = analysisData.bases.size
    private val solutionsDir = analysisData.rootDir

    fun run() =
        buildString {
            append("java -jar $jplagPath  -l $language -r $resultDir -s ")
            if (baseCount != 0) append("-bc .base ")
            append(asPath(solutionsDir))
        }.also {
            logger.info { it }
        }.also(::execute)

    private fun execute(task: String) {
        Runtime.getRuntime().exec(task).waitFor(10, TimeUnit.MINUTES)
    }

}