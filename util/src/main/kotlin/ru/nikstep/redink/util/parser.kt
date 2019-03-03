package ru.nikstep.redink.util

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser

private val parser = Parser.default()

/**
 * Parse [String] to [JsonObject]
 */
fun String.parseAsObject(): JsonObject {
    return parser.parse(this.reader()) as JsonObject
}

/**
 * Parse [String] to [JsonArray]
 */
fun String.parseAsArray(): JsonArray<*> {
    return parser.parse(this.reader()) as JsonArray<*>
}