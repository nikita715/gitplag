package io.gitplag.analysis.client

import io.gitplag.model.data.PreparedAnalysisData
import io.gitplag.util.asPath
import jplag.Program
import jplag.options.CommandLineOptions
import mu.KotlinLogging


/**
 * Client of the JPlag plagiarism analysis service.
 * See https://jplag.ipd.kit.edu
 */
internal class JPlagClient(
    analysisData: PreparedAnalysisData,
    private val resultDir: String
) {

    private val logger = KotlinLogging.logger {}

    private val language = analysisData.language.ofJPlag()
    private val baseCount = analysisData.bases.size
    private val solutionsDir = analysisData.rootDir
    private val resultSize = analysisData.resultSize

    fun run() =
        buildString {
            append("-l $language -r $resultDir -s ")
            if (baseCount != 0) append("-bc .base ")
            if (resultSize != null) append("-m $resultSize ")
            append(asPath(solutionsDir))
        }.also {
            logger.info { it }
        }.also(::execute)

    private fun execute(task: String) {
        val options = CommandLineOptions(task.split(" ").toTypedArray(), null)
        Program(options).run()
    }

}