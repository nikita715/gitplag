package ru.nikstep.redink.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(scanBasePackages = ["ru.nikstep.redink"])
@EnableJpaRepositories("ru.nikstep.redink.model.repo")
@EntityScan("ru.nikstep.redink.model.entity")
@PropertySource("classpath:application.yml")
@EnableScheduling
class RedinkApplication

fun main(args: Array<String>) {
    runApplication<RedinkApplication>(*args)
}
