package ru.nikstep.redink.util

import org.apache.commons.lang3.RandomStringUtils
import kotlin.random.Random

/**
 * Random generator wrapper
 */
class RandomGenerator {

    /**
     * Generate a random alphanumeric tring
     */
    fun randomAlphanumeric(size: Int): String = RandomStringUtils.randomAlphanumeric(size)

    fun randomHexColor(): String = String.format("#%06x", Random.nextInt(0xffffff + 1))

}