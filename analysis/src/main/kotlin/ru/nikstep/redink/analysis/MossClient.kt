package ru.nikstep.redink.analysis

import it.zielke.moji.SocketClient
import mu.KotlinLogging
import java.io.File

class MossClient(
    private val userId: String,
    private val client: SocketClient = SocketClient(),
    private val base: File,
    private val solutions: Collection<File>
) : AnalysisSystemClient {

    constructor(userId: String, files: Pair<File, Collection<File>>) : this(
        userId,
        base = files.first,
        solutions = files.second
    )

    constructor(userId: String, files: PreparedAnalysisFiles) : this(
        userId,
        base = files.base,
        solutions = files.solutions
    )

    private val language: String = base.mossExtension

    private val logger = KotlinLogging.logger {}

    override fun base(base: File): AnalysisSystemClient =
        MossClient(userId, client, base, solutions)

    override fun solutions(solutions: List<File>): AnalysisSystemClient =
        MossClient(userId, client, base, solutions)

    @Synchronized
    override fun analyse(): String {

        Thread.sleep(1000)

        client.userID = userId
        client.language = language

        if (solutions.isEmpty()) {
            throw AnalysisException("Analysis: No solutions for file ${base.canonicalPath}")
        }

        try {
            client.run()

            loadBaseFile(base)
            solutions.forEach { loadFile(it) }

            client.sendQuery()
        } finally {
            client.close()
        }

        return client.resultURL.toString()
    }

    private fun loadBaseFile(file: File) = loadFile(file, isBase = true)

    private fun loadFile(file: File, isBase: Boolean = false) =
        try {
            client.uploadFile(file, isBase)
        } catch (e: Exception) {
            logger.error(
                "Can't load ${if (isBase) "base" else "solutions"} " +
                        "file ${file.name} to moss server", e
            )
        }

}

val File.mossExtension: String
    get() {
        val extension = this.extension
        return when (extension) {
            "java" -> extension
            "cpp" -> "cc"
            else -> throw RuntimeException("Analysis: Moss does not support the \"${this.extension}\" extension")
        }
    }