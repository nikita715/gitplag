package io.gitplag.analysis.analyzer

import io.gitplag.model.data.PreparedAnalysisData
import mossclient.Language
import mossclient.MossClient
import mu.KotlinLogging

/**
 * Client of the Moss plagiarism analysis service.
 * See http://moss.stanford.edu
 */
internal class MossClient(analysisData: PreparedAnalysisData, private val mossId: String) {
    private val language = analysisData.language
    private val bases = analysisData.bases
    private val solutions = analysisData.solutions
    private val logger = KotlinLogging.logger {}
    private val parameters = analysisData.analysisParameters

    @Synchronized
    fun run(): String = MossClient(mossId, Language.valueOf(language.name))
        .submitFiles(bases, isBase = true)
        .submitNamedFiles(solutions.map { it.student to it.file })
        .getResult()

}