package ru.nikstep.redink.model.data

import java.io.File

/**
 * Solution file and its information
 */
class CommittedFile(val file: File, val sha: String, val fileName: String)