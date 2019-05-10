package io.gitplag.util

import net.lingala.zip4j.core.ZipFile
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Files
import java.util.stream.Collectors.toList

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

fun generateDir(randomGenerator: RandomGenerator, path: String): Pair<String, String> {
    val hash = randomGenerator.randomAlphanumeric(10)
    val file = File("$path/$hash")
    Files.deleteIfExists(file.toPath())
    Files.createDirectories(file.toPath())
    val resultDir = file.absolutePath
    return Pair(hash, resultDir)
}

/**
 * Download an archive from the [resourceUrl], unpack it and perform the action in it
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


/**
 * Foreach for regular public files in the [path]
 */
fun forEachFileInDirectory(path: String, action: (File) -> Unit) {
    File(path).innerRegularFiles().forEach { file -> action(file) }
}

/**
 * Find all regular public files in the [File]
 */
fun File.innerRegularFiles(): List<File> =
    Files.walk(this.toPath()).filter { Files.isRegularFile(it) && !Files.isHidden(it) }
        .map { it.toFile() }
        .sorted { o1, o2 -> o1.absolutePath.compareTo(o2.absolutePath) }
        .collect(toList())
