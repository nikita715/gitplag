package ru.nikstep.redink.core.util

import org.springframework.core.env.Environment

fun Environment.safeEnvVar(name: String) = requireNotNull(getProperty(name))