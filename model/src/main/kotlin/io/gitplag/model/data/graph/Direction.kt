package io.gitplag.model.data.graph

/**
 * Arrow direction
 */
enum class Direction {
    FIRST,
    SECOND;

    override fun toString() = name.toLowerCase()
}