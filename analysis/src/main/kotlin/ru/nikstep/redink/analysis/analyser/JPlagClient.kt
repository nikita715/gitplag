package ru.nikstep.redink.analysis.analyser

import mu.KotlinLogging
import ru.nikstep.redink.analysis.PreparedAnalysisFiles
import ru.nikstep.redink.analysis.logJPlag
import ru.nikstep.redink.util.asPath
import ru.nikstep.redink.util.asPathInRoot
import ru.nikstep.redink.util.onlyLastName
import java.util.concurrent.TimeUnit

internal class JPlagClient(
    private val analysisFiles: PreparedAnalysisFiles,
    private val solutionsPath: String,
    private val resultPath: String
) {
    private val logger = KotlinLogging.logger {}

    private val jplagPath = asPath("libs".asPathInRoot(), "jplag.jar")

    fun run() =
        buildString {
            append("java -jar $jplagPath ")
            append("-l ${analysisFiles.language.ofJPlag()} ")
            append("-bc .base ")
            append("-r $resultPath ")
            append("-p ${analysisFiles.fileName.onlyLastName()} ")
            append("-s ")
            append(asPath(solutionsPath, analysisFiles.repoName))
        }.also { task ->
            logger.logJPlag(task) {
                Runtime.getRuntime().exec(task).waitFor(5, TimeUnit.MINUTES)
            }
        }

}