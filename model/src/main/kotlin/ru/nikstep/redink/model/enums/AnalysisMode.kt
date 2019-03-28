package ru.nikstep.redink.model.enums

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import ru.nikstep.redink.model.util.AnalysisModeDeserializer

/**
 * Category of the completeness of the plagiarism analysis
 */
@JsonDeserialize(using = AnalysisModeDeserializer::class)
enum class AnalysisMode(val order: Int) {
    LINK(1),
    PAIRS(2),
    FULL(3);
}