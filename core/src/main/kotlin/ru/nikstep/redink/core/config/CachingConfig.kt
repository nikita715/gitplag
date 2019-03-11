package ru.nikstep.redink.core.config

import com.google.common.cache.CacheBuilder
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

/**
 * Application caching configuration
 */
@Configuration
@EnableCaching
class CachingConfig {

    /**
     * [CacheManager] bean for github passwords
     */
    @Bean
    fun cacheManager(): CacheManager {
        return object : ConcurrentMapCacheManager("githubAccessTokens") {
            override fun createConcurrentMapCache(name: String): Cache = ConcurrentMapCache(
                name,
                CacheBuilder.newBuilder()
                    .expireAfterWrite(30, TimeUnit.MINUTES)
                    .build<Any, Any>().asMap(),
                false
            )

        }
    }
}