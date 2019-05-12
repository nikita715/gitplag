package io.gitplag.analysis

import io.gitplag.model.data.AnalysisSettings
import io.gitplag.model.entity.Analysis
import java.time.LocalDateTime

fun analysisFilesDirectoryName(analysisSettings: AnalysisSettings, date: LocalDateTime) =
    analysisSettings.repository.name + "/" + date.toString().onlyDigits()

fun analysisFilesDirectoryName(analysis: Analysis) =
    analysis.repository.name + "/" + analysis.executionDate.toString().onlyDigits()

private fun String.onlyDigits() = this.replace("[^\\d]*".toRegex(), "")
