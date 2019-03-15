package ru.nikstep.redink.model.data

import ru.nikstep.redink.util.Language
import java.io.File

/**
 * Class for storing information about the file
 * that must be analysed for plagiarism
 */
class PreparedAnalysisData(

    /**
     * Name of the repo with the file
     */
    val repoName: String,

    /**
     * Language of the file
     */
    val language: Language,

    /**
     * Base files and solution files that were created by students
     */
    val bases: List<File>,

    /**
     * Solution files that were created by students
     */
    val solutions: List<Solution>
)

class Solution(
    val student: String,
    val fileName: String,
    val file: File,
    val files: List<String> = emptyList(),
    val lengths: List<Int> = emptyList(),
    val sha: String
)

fun findByStudent(solutions: List<Solution>, student: String) =
    requireNotNull(solutions.find { it.student == student })