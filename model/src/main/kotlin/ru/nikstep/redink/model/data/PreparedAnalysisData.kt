package ru.nikstep.redink.model.data

import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language
import java.io.File

/**
 * Class for storing information about the file
 * that must be analysed for plagiarism
 */
class PreparedAnalysisData(

    /**
     * Name of git service with the repo
     */
    val gitService: GitProperty,

    /**
     * Name of the repo with the file
     */
    val repoName: String,

    /**
     * Language of the file
     */
    val language: Language,

    /**
     * Root dir with all files
     */
    val rootDir: String,

    /**
     * Base files and solution files that were created by students
     */
    val bases: List<File>,

    /**
     * Solution files that were created by students
     */
    val solutions: List<Solution>
)