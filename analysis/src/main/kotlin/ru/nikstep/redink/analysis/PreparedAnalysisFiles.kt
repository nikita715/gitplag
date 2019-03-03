package ru.nikstep.redink.analysis

import ru.nikstep.redink.util.Language
import java.io.File

/**
 * Class for storing information about files
 * that must be analysed for plagiarism
 */
class PreparedAnalysisFiles(

    /**
     * Name of the file that must be analysed
     */
    val fileName: String,

    /**
     * Name of the repo with the file
     */
    val repoName: String,

    /**
     * Language of the file
     */
    val language: Language,

    /**
     * Base file of the file
     */
    val base: File,

    /**
     * Solution files that were created by students
     */
    val solutions: Collection<File>
)