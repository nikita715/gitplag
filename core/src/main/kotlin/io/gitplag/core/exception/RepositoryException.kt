package io.gitplag.core.exception

/**
 * Common exception of repository requests.
 */
class RepositoryException(message: String, throwable: Throwable) : RuntimeException(message, throwable)