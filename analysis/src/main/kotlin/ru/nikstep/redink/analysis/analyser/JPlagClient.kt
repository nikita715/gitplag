package ru.nikstep.redink.analysis.analyser

import mu.KotlinLogging
import ru.nikstep.redink.analysis.PreparedAnalysisFiles
import ru.nikstep.redink.analysis.logJPlag
import ru.nikstep.redink.util.asPath
import ru.nikstep.redink.util.asPathInRoot
import ru.nikstep.redink.util.onlyLastName
import java.util.concurrent.TimeUnit

internal class JPlagClient(
    analysisFiles: PreparedAnalysisFiles,
    private val solutionsPath: String,
    private val resultPath: String
) {

    private val language = analysisFiles.language.ofJPlag()
    private val repoName = analysisFiles.repoName
    private val fileName = analysisFiles.fileName.onlyLastName()

    private val logger = KotlinLogging.logger {}
    private val jplagPath = asPath("libs".asPathInRoot(), "jplag.jar")

    fun run() =
        buildString {
            append("java -jar $jplagPath  -l $language -bc .base -r $resultPath  -p $fileName -s ")
            append(asPath(solutionsPath, repoName))
        }.also { task ->
            logger.logJPlag(task) {
                execute(task)
            }
        }

    private fun execute(task: String) {
        Runtime.getRuntime().exec(task).waitFor(1, TimeUnit.MINUTES)
    }

}