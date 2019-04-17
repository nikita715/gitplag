package io.gitplag.analysis.analyzer

import io.gitplag.model.data.PreparedAnalysisData
import io.gitplag.util.asPath
import io.gitplag.util.asPathInRoot
import mu.KotlinLogging
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
    private val parameters = analysisData.analysisParameters

    fun run() =
        buildString {
            append("java -jar $jplagPath  -l $language -r $resultDir -s $parameters ")
            if (baseCount != 0) append("-bc .base ")
            append(asPath(solutionsDir))
        }.also {
            logger.info { it }
        }.also(::execute)

    private fun execute(task: String) {
        Runtime.getRuntime().exec(task).waitFor(10, TimeUnit.MINUTES)
    }

}