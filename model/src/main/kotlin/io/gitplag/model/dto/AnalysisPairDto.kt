package io.gitplag.model.dto

import io.gitplag.model.entity.AnalysisPair

/**
 * Dto for [AnalysisPair]
 */
class AnalysisPairDto(val files1: Collection<FileDto>, val files2: Collection<FileDto>, val pair: AnalysisPair)
