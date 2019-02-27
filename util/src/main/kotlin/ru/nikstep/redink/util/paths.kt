package ru.nikstep.redink.util

import java.io.File

private val separator = System.getProperty("file.separator")
private val rootDir = System.getProperty("user.dir")
private const val projectName = "redink"

fun String.asFileInRoot(): String {
    val root = File(rootDir.replaceAfterLast(projectName, "")).toPath()
    return root.resolve(this).toFile().absolutePath
}

fun String.pathTo(): String {
    return this.substringBeforeLast(separator)
}

fun String.onlyLastName(): String {
    return this.substringAfterLast(separator)
}