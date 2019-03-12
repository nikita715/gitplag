package ru.nikstep.redink.core.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Mvc config of the application
 */
@Configuration
class MvcConfig : WebMvcConfigurer {

    @Value("\${redink.jplagResultDir}")
    private lateinit var jplagResultDir: String

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "OPTIONS", "PUT")
            .allowedHeaders(
                "Content-Type", "X-Requested-With", "accept", "Origin",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"
            )
            .exposedHeaders("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials")
            .allowCredentials(true).maxAge(3600)
    }


    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/static/**", "/resources/**")
            .addResourceLocations("classpath:/static/")
        registry.addResourceHandler("/jplagresult/**").addResourceLocations("file:$jplagResultDir")
    }
}