package ru.nikstep.redink.core.graphql

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseValueException
import graphql.schema.GraphQLScalarType
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class LocalDateTimeScalarType : GraphQLScalarType(
    "LocalDateTime",
    "LocalDateTime value",
    object : Coercing<LocalDateTime, String> {
        override fun parseValue(input: Any?): LocalDateTime {
            if (input is String) {
                return LocalDateTime.parse(input)
            } else {
                throw CoercingParseValueException()
            }
        }

        override fun parseLiteral(input: Any?): LocalDateTime =
            if (input is StringValue) {
                LocalDateTime.parse(input.value)
            } else {
                throw CoercingParseValueException()
            }


        override fun serialize(dataFetcherResult: Any?): String =
            if (dataFetcherResult is LocalDateTime) {
                dataFetcherResult.toString()
            } else {
                throw CoercingParseValueException()
            }


    }
)