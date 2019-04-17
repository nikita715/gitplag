package io.gitplag.model.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import io.gitplag.model.enums.AnalysisMode
import io.gitplag.model.enums.AnalyzerProperty
import io.gitplag.model.enums.GitProperty
import io.gitplag.model.enums.Language

/**
 * Jackson deserializer of [GitProperty]
 */
class GitPropertyDeserializer : JsonDeserializer<GitProperty>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): GitProperty? =
        p?.valueAsString?.toUpperCase()?.let { GitProperty.valueOf(it) }
}

/**
 * Jackson deserializer of [Language]
 */
class LanguageDeserializer : JsonDeserializer<Language>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Language? =
        p?.valueAsString?.toUpperCase()?.let { Language.valueOf(it) }
}

/**
 * Jackson deserializer of [AnalysisMode]
 */
class AnalysisModeDeserializer : JsonDeserializer<AnalysisMode>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): AnalysisMode? =
        p?.valueAsString?.toUpperCase()?.let { AnalysisMode.valueOf(it) }
}

/**
 * Jackson deserializer of [AnalyzerProperty]
 */
class AnalyzerPropertyDeserializer : JsonDeserializer<AnalyzerProperty>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): AnalyzerProperty? =
        p?.valueAsString?.toUpperCase()?.let { AnalyzerProperty.valueOf(it) }
}
