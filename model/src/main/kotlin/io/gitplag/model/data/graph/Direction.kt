package io.gitplag.model.data.graph

enum class Direction {
    FIRST,
    SECOND;

    override fun toString() = name.toLowerCase()
}