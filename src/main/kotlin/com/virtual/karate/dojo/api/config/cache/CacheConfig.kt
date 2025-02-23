package com.virtual.karate.dojo.api.config.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.virtual.karate.dojo.api.utils.Constants.LOGIN_ATTEMPTS_CACHE
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {
    @Bean(name = ["cacheManagerLogin"])
    fun cacheManagerLogin(): CacheManager {
        val cacheManager = CaffeineCacheManager(LOGIN_ATTEMPTS_CACHE)
        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(100)
        )
        return cacheManager
    }
}