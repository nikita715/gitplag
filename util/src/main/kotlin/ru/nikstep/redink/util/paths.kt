package ru.nikstep.redink.util

import java.io.File
import java.nio.file.Paths

private val separator = System.getProperty("file.separator")

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
        Paths.get(it)
    }.run {
        if (toFile().exists()) {
            toString()
        } else {
            File(toString().substringBeforeLast("/")).absolutePath
        }
    }
