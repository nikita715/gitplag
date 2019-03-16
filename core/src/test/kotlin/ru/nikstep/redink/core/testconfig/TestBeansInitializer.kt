package ru.nikstep.redink.core.testconfig

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import ru.nikstep.redink.core.beans.analysisBeans
import ru.nikstep.redink.core.beans.coreBeans
import ru.nikstep.redink.core.beans.gitBeans

class TestBeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
    override fun initialize(context: GenericApplicationContext) {
        coreBeans.initialize(context)
        gitBeans.initialize(context)
        analysisBeans.initialize(context)
    }
}