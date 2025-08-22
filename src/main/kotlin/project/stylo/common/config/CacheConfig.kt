package project.stylo.common.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {
    companion object {
        private const val CATEGORY_CACHE = "categoryCache"
    }

    @Bean
    fun cacheManager(): CacheManager {
        val caffeine = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(24))
            .maximumSize(1000)
            .recordStats()

        val cacheManager = CaffeineCacheManager(CATEGORY_CACHE)
        cacheManager.setCaffeine(caffeine)
        return cacheManager
    }
}