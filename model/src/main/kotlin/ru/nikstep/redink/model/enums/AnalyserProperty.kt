package ru.nikstep.redink.model.enums

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import ru.nikstep.redink.model.util.AnalyserPropertyDeserializer

/**
 * Name of a plagiarism analyser
 */
@JsonDeserialize(using = AnalyserPropertyDeserializer::class)
enum class AnalyserProperty {
    MOSS,
    JPLAG
}