package io.gitplag.model.dto

import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language

/**
 * Dto for [Repository] class
 */
data class OutputRepositoryDto(
    val id: Long,
    val filePatterns: Collection<String>,
    val name: String,
    val analyzer: AnalyzerProperty,
    val gitService: GitProperty,
    val language: Language,
    val analysisMode: AnalysisMode,
    val autoCloningEnabled: Boolean,
    val mossParameters: String,
    val jplagParameters: String
) {
    constructor(repo: Repository) : this(
        id = repo.id,
        filePatterns = repo.filePatterns,
        name = repo.name,
        analyzer = repo.analyzer,
        gitService = repo.gitService,
        language = repo.language,
        analysisMode = repo.analysisMode,
        autoCloningEnabled = repo.autoCloningEnabled,
        mossParameters = repo.mossParameters,
        jplagParameters = repo.jplagParameters
    )
}