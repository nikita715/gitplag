package io.gitplag.model.enums

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.gitplag.model.util.GitPropertyDeserializer

/**
 * Name of the git repository
 */
@JsonDeserialize(using = GitPropertyDeserializer::class)
enum class GitProperty {
    GITHUB,
    BITBUCKET,
    GITLAB;

    override fun toString(): String = name.toLowerCase()
}