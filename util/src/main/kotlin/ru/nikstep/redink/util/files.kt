package ru.nikstep.redink.util

import java.nio.file.Files

fun <T> inTempDirectory(action: (dirPath: String) -> T): T {
    val file = Files.createTempDirectory("").toFile()
    val result = action(file.absolutePath)
    file.deleteRecursively()
    return result
}