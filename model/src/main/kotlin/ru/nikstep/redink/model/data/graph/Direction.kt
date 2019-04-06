package ru.nikstep.redink.model.data.graph

enum class Direction {
    FIRST,
    SECOND;

    override fun toString() = name.toLowerCase()
}