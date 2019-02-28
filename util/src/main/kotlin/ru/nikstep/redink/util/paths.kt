package ru.nikstep.redink.util

import java.io.File

private val separator = System.getProperty("file.separator")
private val currentRootDir = System.getProperty("user.dir")
private const val rootName = "redink"

fun asPath(vararg part: String): String {
    return part.joinToString(separator = separator)
}

fun String.asPathInRoot(): String {
    val root = File(currentRootDir.replaceAfterLast(rootName, "")).toPath()
    return root.resolve(this).toFile().absolutePath
}

fun String.pathTo(): String {
    return this.substringBeforeLast(separator)
}

fun String.onlyLastName(): String {
    return this.substringAfterLast(separator)
}