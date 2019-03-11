package ru.nikstep.redink.core.util

import com.google.common.cache.CacheBuilder
import org.springframework.cache.Cache
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import java.util.concurrent.TimeUnit

class TokenCacheManager(name: String) : ConcurrentMapCacheManager(name) {
    override fun createConcurrentMapCache(name: String): Cache = ConcurrentMapCache(
        name,
        CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build<Any, Any>().asMap(),
        false
    )
}