package ru.nikstep.redink.analysis

import it.zielke.moji.SocketClient
import mu.KotlinLogging
import ru.nikstep.redink.util.Language
import java.io.File

class MossClient(
    private val userId: String,
    private val client: SocketClient = SocketClient(),
    private val base: File,
    private val solutions: Collection<File>,
    private val language: Language = Language.TEXT
) : AnalysisSystemClient {

    constructor(userId: String, files: Pair<File, Collection<File>>) : this(
        userId,
        base = files.first,
        solutions = files.second
    )

    constructor(userId: String, files: PreparedAnalysisFiles) : this(
        userId,
        base = files.base,
        solutions = files.solutions,
        language = files.language
    )

    private val logger = KotlinLogging.logger {}

    override fun base(base: File): AnalysisSystemClient =
        MossClient(userId, client, base, solutions)

    override fun solutions(solutions: List<File>): AnalysisSystemClient =
        MossClient(userId, client, base, solutions)

    @Synchronized
    override fun analyse(): String {

        Thread.sleep(1000)

        client.userID = userId
        client.language = language.ofMoss()

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

        val resultUrl = client.resultURL.toString()

        logger.info { "Analysis: performed new analysis at $resultUrl" }

        return resultUrl
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