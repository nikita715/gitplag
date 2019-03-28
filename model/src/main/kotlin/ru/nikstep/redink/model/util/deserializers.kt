package ru.nikstep.redink.model.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import ru.nikstep.redink.model.enums.AnalyserProperty
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language

class GitPropertyDeserializer : JsonDeserializer<GitProperty>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): GitProperty? =
        p?.valueAsString?.toUpperCase()?.let { GitProperty.valueOf(it) }
}

class LanguageDeserializer : JsonDeserializer<Language>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Language? =
        p?.valueAsString?.toUpperCase()?.let { Language.valueOf(it) }
}

class AnalysisModeDeserializer : JsonDeserializer<AnalysisMode>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): AnalysisMode? =
        p?.valueAsString?.toUpperCase()?.let { AnalysisMode.valueOf(it) }
}

class AnalyserPropertyDeserializer : JsonDeserializer<AnalyserProperty>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): AnalyserProperty? =
        p?.valueAsString?.toUpperCase()?.let { AnalyserProperty.valueOf(it) }
}