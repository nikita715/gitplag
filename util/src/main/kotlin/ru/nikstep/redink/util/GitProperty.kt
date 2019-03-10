package ru.nikstep.redink.util

enum class GitProperty {
    GITHUB,
    BITBUCKET,
    GITLAB;

    override fun toString(): String = this.name.toLowerCase()
}