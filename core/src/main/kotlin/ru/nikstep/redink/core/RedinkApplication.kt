package ru.nikstep.redink.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import ru.nikstep.redink.core.beans.analysisBeans
import ru.nikstep.redink.core.beans.coreBeans
import ru.nikstep.redink.core.beans.gitBeans

/**
 * The main Spring Boot application class
 */
@SpringBootApplication(scanBasePackages = ["ru.nikstep.redink"])
@EnableJpaRepositories("ru.nikstep.redink.model.repo")
@EntityScan("ru.nikstep.redink.model.entity")
@PropertySource("classpath:application.yml")
@EnableCaching
@EnableScheduling
@EnableAsync
class RedinkApplication

/**
 * The application entry point
 */
fun main(args: Array<String>) {
    runApplication<RedinkApplication> {
        addInitializers(
            analysisBeans,
            gitBeans,
            coreBeans
        )
    }
}
