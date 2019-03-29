package ru.nikstep.redink.model.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import ru.nikstep.redink.model.enums.AnalyserProperty
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language

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
* Jackson deserializer of [AnalyserProperty]
*/
class AnalyserPropertyDeserializer : JsonDeserializer<AnalyserProperty>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): AnalyserProperty? =
        p?.valueAsString?.toUpperCase()?.let { AnalyserProperty.valueOf(it) }
}
