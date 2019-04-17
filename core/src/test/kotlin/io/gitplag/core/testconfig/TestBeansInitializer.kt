package io.gitplag.core.testconfig

import io.gitplag.core.beans.analysisBeans
import io.gitplag.core.beans.coreBeans
import io.gitplag.core.beans.gitBeans
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext

/**
 * Composed bean initializers for tests
 */
class TestBeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(context: GenericApplicationContext) {
        coreBeans.initialize(context)
        gitBeans.initialize(context)
        analysisBeans.initialize(context)
    }
}