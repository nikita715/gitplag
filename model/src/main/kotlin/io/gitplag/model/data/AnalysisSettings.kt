package io.gitplag.model.data

import io.gitplag.model.entity.Repository
import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.Language
import java.time.LocalDateTime

/**
 * Input analysis properties
 */
class AnalysisSettings(
    val repository: Repository,
    val branch: String,
    val executionDate: LocalDateTime = LocalDateTime.now()
) {
    var analyzer: AnalyzerProperty = repository.analyzer
        private set
    var language: Language = repository.language
        private set
    var analysisMode: AnalysisMode = repository.analysisMode
        private set
    var additionalRepositories: List<Long> = emptyList()
        private set
    var updateFiles: Boolean = false
        private set
    var maxResultSize: Int? = null
        private set

    var minResultPercentage: Int = 0
        private set

    constructor(
        repository: Repository,
        branch: String,
        analyzer: AnalyzerProperty? = repository.analyzer,
        language: Language?,
        analysisMode: AnalysisMode?,
        updateFiles: Boolean?,
        additionalRepositories: List<Long>?,
        maxResultSize: Int? = null,
        minResultPercentage: Int? = null
    ) : this(repository, branch) {
        this.analyzer = analyzer ?: repository.analyzer
        this.language = language ?: repository.language
        this.analysisMode = analysisMode ?: repository.analysisMode
        this.updateFiles = updateFiles ?: false
        this.additionalRepositories = additionalRepositories ?: emptyList()
        this.maxResultSize = maxResultSize
        this.minResultPercentage = minResultPercentage ?: 0
    }
}