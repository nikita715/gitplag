package io.gitplag.model.dto

import io.gitplag.model.entity.AnalysisPair
import java.time.LocalDateTime

/**
 * Dto for [AnalysisPair] without line matches
 */
class AnalysisResultSimplePairDto(
    val id: Long,
    val student1: String,
    val student2: String,
    val percentage: Int,
    val minPercentage: Int,
    val maxPercentage: Int,
    val createdAt1: LocalDateTime,
    val createdAt2: LocalDateTime
) {
    constructor(analysisPair: AnalysisPair) : this(
        analysisPair.id,
        analysisPair.student1,
        analysisPair.student2,
        analysisPair.percentage,
        analysisPair.minPercentage,
        analysisPair.maxPercentage,
        analysisPair.createdAt1,
        analysisPair.createdAt2
    )
}