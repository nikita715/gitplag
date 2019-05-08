package io.gitplag.core.rest

/**
 * Git 403 exception
 */
class GitAccessException(message: String, throwable: Throwable) : RuntimeException(message, throwable)