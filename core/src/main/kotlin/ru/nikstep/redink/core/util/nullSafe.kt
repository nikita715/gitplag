package ru.nikstep.redink.core.util

import org.springframework.core.env.Environment

internal fun Environment.safeEnvVar(name: String) = requireNotNull(getProperty(name))