package ru.nikstep.redink.model.enums

/**
 * Category of the completeness of the plagiarism analysis
 */
enum class AnalysisMode(val order: Int) {
    LINK(1),
    PAIRS(2),
    FULL(3);
}