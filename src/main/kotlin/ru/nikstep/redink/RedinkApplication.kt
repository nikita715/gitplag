package ru.nikstep.redink

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RedinkApplication

fun main(args: Array<String>) {
    runApplication<RedinkApplication>(*args)
}
