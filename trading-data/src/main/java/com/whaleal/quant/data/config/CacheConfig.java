package com.whaleal.quant.data.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * 缓存配置类
 * 配置多级缓存：本地缓存（Caffeine）和分布式缓存（Redis）
 *
 * @author whaleal
 * @version 1.0.0
 */
@Configuration
public class CacheConfig {
    
    /**
     * 本地缓存管理器
     * 使用Caffeine作为本地缓存实现
     *
     * @return 缓存管理器
     */
    @Bean
    public CacheManager localCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10000) // 最大缓存条目数
                .expireAfterWrite(Duration.ofMinutes(10)) // 写入后过期时间
                .expireAfterAccess(Duration.ofMinutes(5)) // 访问后过期时间
                .recordStats() // 记录缓存统计信息
        );
        return cacheManager;
    }
    
    /**
     * 分布式缓存管理器
     * 使用Redis作为分布式缓存实现
     *
     * @param redisConnectionFactory Redis连接工厂
     * @return 缓存管理器
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // 缓存过期时间
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .prefixCacheNameWith("stocks:"); // 缓存键前缀
        
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
