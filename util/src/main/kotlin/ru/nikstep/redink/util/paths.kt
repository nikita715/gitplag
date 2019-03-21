package ru.nikstep.redink.util

import java.nio.file.Paths

private val separator = System.getProperty("file.separator")
private val rootDirRegex = "/\\w+/\\w+$".toRegex()

/**
 * Combine [parts] to file system path with [separator]
 */
fun asPath(vararg parts: Any): String {
    return parts.joinToString(separator = separator)
}

/**
 * Recognize the folder in the project root and return the path to it
 */
fun String.asPathInRoot(): String =
    let {
        Paths.get(it).toAbsolutePath()
    }.run {
        if (toFile().exists()) {
            toString()
        } else {
            toString().replace(rootDirRegex, "/libs")
        }
    }
