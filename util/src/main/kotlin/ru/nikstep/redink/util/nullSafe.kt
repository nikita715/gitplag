package ru.nikstep.redink.util

import org.springframework.core.env.Environment

fun Environment.safeEnvVar(name: String) = requireNotNull(getProperty(name))