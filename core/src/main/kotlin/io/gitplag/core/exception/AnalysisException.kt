package io.gitplag.core.exception

/**
 * Common exception of analysis requests.
 */
class AnalysisException(message: String, throwable: Throwable) : RuntimeException(message, throwable)