package ru.nikstep.redink.analysis

/**
 * Common exception at the plagiarism analysis
 */
class AnalysisException(message: String?, cause: Throwable?) : RuntimeException(message, cause) {
    constructor(message: String) : this(message, null)
    constructor(throwable: Throwable) : this(null, throwable)
}