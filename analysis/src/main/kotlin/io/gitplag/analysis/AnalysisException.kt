package io.gitplag.analysis

/**
 * A common analysis exception
 */
class AnalysisException(message: String, throwable: Throwable?) : RuntimeException(message, throwable)