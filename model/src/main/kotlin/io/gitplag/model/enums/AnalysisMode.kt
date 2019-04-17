package io.gitplag.model.enums

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.gitplag.model.util.AnalysisModeDeserializer

/**
 * Category of the completeness of the plagiarism analysis
 */
@JsonDeserialize(using = AnalysisModeDeserializer::class)
enum class AnalysisMode(val order: Int) {
    LINK(1),
    PAIRS(2),
    FULL(3);

    override fun toString(): String = name.toLowerCase()
}