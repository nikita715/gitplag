package io.gitplag.core

import io.gitplag.core.beans.analysisBeans
import io.gitplag.core.beans.coreBeans
import io.gitplag.core.beans.gitBeans
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * The main Spring Boot application class
 */
@SpringBootApplication(scanBasePackages = ["io.gitplag"])
@EnableJpaRepositories("io.gitplag.model.repo")
@EntityScan("io.gitplag.model.entity")
@PropertySource("classpath:application.yml")
@EnableScheduling
@EnableAsync
class GitplagApplication

/**
 * The application entry point
 */
fun main() {
    runApplication<GitplagApplication> {
        addInitializers(
            analysisBeans,
            gitBeans,
            coreBeans
        )
    }
}
