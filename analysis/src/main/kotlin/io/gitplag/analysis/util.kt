package io.gitplag.analysis

import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.entity.Analysis
import java.time.LocalDateTime

/**
 * Define path to source code files of the [analysisSettings]
 */
fun analysisFilesDirectoryName(analysisSettings: AnalysisSettings) =
    analysisSettings.repository.name + "/" + analysisSettings.executionDate.toString().onlyDigits()

/**
 * Define path to source code files of the [analysis]
 */
fun analysisFilesDirectoryName(analysis: Analysis) =
    analysis.repository.name + "/" + analysis.executionDate.toString().onlyDigits()

/**
 * Define path to source code files by [repoName] and [executionDate]
 */
fun analysisFilesDirectoryName(repoName: String, executionDate: LocalDateTime) =
    repoName + "/" + executionDate.toString().onlyDigits()

private fun String.onlyDigits() = this.replace("[^\\d]*".toRegex(), "")

/**
 * Info about the repo from the [analysisSettings]
 */
fun repoInfo(analysisSettings: AnalysisSettings): String =
    analysisSettings.run { "Repo ${repository.name}, Branch $branch." }