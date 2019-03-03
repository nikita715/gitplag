package ru.nikstep.redink.util

import java.nio.file.Files

fun <T> inTempDirectory(action: (dirPath: String) -> T): T {
    val file = Files.createTempDirectory("").toFile()
    try {
        return action(file.absolutePath)
    } finally {
        file.deleteRecursively()
    }
}