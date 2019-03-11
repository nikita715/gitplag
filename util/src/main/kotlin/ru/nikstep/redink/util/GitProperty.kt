package ru.nikstep.redink.util

/**
 * Name of the git repository
 */
enum class GitProperty {
    GITHUB,
    BITBUCKET,
    GITLAB;

    override fun toString(): String = this.name.toLowerCase()
}