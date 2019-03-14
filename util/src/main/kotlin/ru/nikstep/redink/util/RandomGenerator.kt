package ru.nikstep.redink.util

import org.apache.commons.lang3.RandomStringUtils

class RandomGenerator {

    fun randomAlphanumeric(size: Int): String = RandomStringUtils.randomAlphanumeric(size)

}