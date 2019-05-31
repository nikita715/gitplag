package io.gitplag.model.dto

import io.gitplag.model.entity.AnalysisPair
import java.time.LocalDateTime

/**
 * Dto for [AnalysisPair]
 */
class AnalysisPairDto(
    val id: Long,
    val student1: String,
    val student2: String,
    val percentage: Int,
    val createdAt1: LocalDateTime,
    val createdAt2: LocalDateTime,
    val lines: List<AnalysisPairLinesDto>
) {
    constructor(analysisPair: AnalysisPair, nameMap: Map<String, String> = emptyMap()) : this(
        analysisPair.id,
        nameMap[analysisPair.student1] ?: analysisPair.student1,
        nameMap[analysisPair.student2] ?: analysisPair.student2,
        analysisPair.percentage,
        analysisPair.createdAt1,
        analysisPair.createdAt2,
        analysisPair.analysisPairLines.map { AnalysisPairLinesDto(it) }.sortedBy { it.from1 }
    )
}