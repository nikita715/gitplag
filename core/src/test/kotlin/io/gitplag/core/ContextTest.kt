package io.gitplag.core

import io.gitplag.core.testconfig.TestBeansInitializer
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * Spring context test
 */
@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = [GitplagApplication::class], initializers = [TestBeansInitializer::class])
@TestPropertySource("classpath:application-test.properties")
class ContextTest {

    /**
     * Try to run the application
     */
    @Test
    fun contextTest() {
    }
}