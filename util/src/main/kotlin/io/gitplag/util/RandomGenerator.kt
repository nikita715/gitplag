package io.gitplag.util

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

    /**
     * Generate a random hex color
     */
    fun randomHexColor(): String = String.format("#%06x", Random.nextInt(0xffffff + 1))

}