package ru.nikstep.redink.util

import java.io.File

private val separator = System.getProperty("file.separator")
private val currentRootDir = System.getProperty("user.dir")
private const val rootName = "redink"

/**
 * Combine [parts] to file system path with [separator]
 */
fun asPath(vararg parts: String): String {
    return parts.joinToString(separator = separator)
}

/**
 * Recognize the folder in the project root and return the path to it
 */
fun String.asPathInRoot(): String {
    val root = File(currentRootDir.replaceAfterLast(rootName, "")).toPath()
    return root.resolve(this).toFile().absolutePath
}

/**
 * Replace path elements before the file name
 */
fun String.pathTo(): String {
    return this.substringBeforeLast(separator)
}

/**
 * Replace the file name in the string path
 */
fun String.onlyLastName(): String {
    return this.substringAfterLast(separator)
}