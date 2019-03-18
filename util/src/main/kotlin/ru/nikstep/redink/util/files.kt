package ru.nikstep.redink.util

import net.lingala.zip4j.core.ZipFile
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Files

/**
 * Perform the [action] in a temp directory
 */
fun <T> inTempDirectory(action: (dirPath: String) -> T): T {
    val file = Files.createTempDirectory("").toFile()
    try {
        return action(file.absolutePath)
    } finally {
        file.deleteRecursively()
    }
}

/**
 * Perform the [action] in a temp directory
 */
fun <T> downloadAndUnpackZip(resourceUrl: String, action: (unpackedDir: String) -> T): T =
    inTempDirectory { tempDir ->
        val zipFile = File("$tempDir/zip.zip")
        BufferedInputStream(URL(resourceUrl).openStream())
            .use { inputStream ->
                val bytes = inputStream.readBytes()
                FileOutputStream(zipFile).use {
                    it.write(bytes)
                    ZipFile(zipFile).extractAll(tempDir)
                }
            }
        zipFile.delete()
        action(tempDir)
    }
