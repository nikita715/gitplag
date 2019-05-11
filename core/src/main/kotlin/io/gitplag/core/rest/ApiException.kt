package io.gitplag.core.rest

/**
 * Git 403 exception
 */
class ApiException(throwable: Throwable) : RuntimeException("Exception during an api call", throwable)