package io.gitplag.model.enums

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.gitplag.model.util.AnalyzerPropertyDeserializer

/**
 * Name of a plagiarism analyzer
 */
@JsonDeserialize(using = AnalyzerPropertyDeserializer::class)
enum class AnalyzerProperty {
    MOSS,
    JPLAG;

    override fun toString(): String = name.toLowerCase()
}