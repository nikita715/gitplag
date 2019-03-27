package ru.nikstep.redink.util

import org.apache.commons.lang3.RandomStringUtils

/**
 * Random generator wrapper
 */
class RandomGenerator {

    /**
     * Generate a random alphanumeric tring
     */
    fun randomAlphanumeric(size: Int): String = RandomStringUtils.randomAlphanumeric(size)

}