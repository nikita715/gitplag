package io.gitplag.model.dto

import io.gitplag.model.entity.AnalysisPairLines

class AnalysisPairLinesDto(
    id: Long,
    val from1: Int,
    val to1: Int,
    val from2: Int,
    val to2: Int,
    val fileName1: String,
    val fileName2: String
) {
    constructor(analysisPairLines: AnalysisPairLines) : this(
        analysisPairLines.id, analysisPairLines.from1, analysisPairLines.to1,
        analysisPairLines.from2, analysisPairLines.to2,
        analysisPairLines.fileName1, analysisPairLines.fileName2
    )
}