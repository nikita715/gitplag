package ru.nikstep.redink.analysis.analyser

import mu.KotlinLogging
import ru.nikstep.redink.analysis.PreparedAnalysisFiles
import ru.nikstep.redink.util.asPath
import ru.nikstep.redink.util.asPathInRoot
import ru.nikstep.redink.util.onlyLastName
import java.util.concurrent.TimeUnit

/**
 * Client of the JPlag plagiarism analysis service.
 * See https://jplag.ipd.kit.edu
 */
internal class JPlagClient(
    analysisFiles: PreparedAnalysisFiles,
    private val solutionsPath: String,
    private val resultPath: String
) {

    private val logger = KotlinLogging.logger {}
    private val jplagPath = asPath("libs".asPathInRoot(), "jplag.jar")

    private val language = analysisFiles.language.ofJPlag()
    private val repoName = analysisFiles.repoName
    private val fileName = analysisFiles.fileName.onlyLastName()

    fun run() =
        buildString {
            append("java -jar $jplagPath  -l $language -bc .base -r $resultPath  -p $fileName -s ")
            append(asPath(solutionsPath, repoName))
        }.also { task ->
            logged(task) {
                execute(task)
            }
        }

    private inline fun logged(task: String, action: () -> Unit) {
        logger.info { "Analysis: start execution of $task" }
        action()
        logger.info { "Analysis: JPlag executed successfully" }
    }

    private fun execute(task: String) {
        Runtime.getRuntime().exec(task).waitFor(1, TimeUnit.MINUTES)
    }

}