package ru.nikstep.redink.core.testconfig

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

import javax.sql.DataSource

@Configuration
class EmbeddedPgConfig {

    @Bean("embeddedPg")
    @Primary
    fun dataSource(): DataSource {
        val postgres = EmbeddedPostgres.start()
        postgres.templateDatabase.connection
            .createStatement().execute("CREATE DATABASE PLAGAN")
        return postgres.getDatabase("postgres", "plagan")
    }

}