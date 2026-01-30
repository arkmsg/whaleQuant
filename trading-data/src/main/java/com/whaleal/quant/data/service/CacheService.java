package com.whaleal.quant.data.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 缓存服务类
 * 封装多级缓存的操作，提供统一的缓存访问接口
 *
 * @author whaleal
 * @version 1.0.0
 */
@Slf4j
@Service
public class CacheService {
    
    @Autowired
    private CacheManager localCacheManager;
    
    @Autowired
    private CacheManager redisCacheManager;
    
    /**
     * 从缓存中获取数据
     * 优先从本地缓存获取，本地缓存未命中则从Redis获取
     *
     * @param cacheName 缓存名称
     * @param key 缓存键
     * @param <T> 数据类型
     * @return 缓存数据
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String cacheName, String key) {
        // 1. 从本地缓存获取
        Cache localCache = localCacheManager.getCache(cacheName);
        if (localCache != null) {
            T value = (T) localCache.get(key, Object.class);
            if (value != null) {
                log.debug("从本地缓存获取数据: {}:{}", cacheName, key);
                return Optional.of(value);
            }
        }
        
        // 2. 从Redis缓存获取
        Cache redisCache = redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            T value = (T) redisCache.get(key, Object.class);
            if (value != null) {
                log.debug("从Redis缓存获取数据: {}:{}", cacheName, key);
                // 将数据同步到本地缓存
                if (localCache != null) {
                    localCache.put(key, value);
                }
                return Optional.of(value);
            }
        }
        
        log.debug("缓存未命中: {}:{}", cacheName, key);
        return Optional.empty();
    }
    
    /**
     * 向缓存中存储数据
     * 同时存储到本地缓存和Redis缓存
     *
     * @param cacheName 缓存名称
     * @param key 缓存键
     * @param value 缓存值
     */
    public void put(String cacheName, String key, Object value) {
        // 1. 存储到本地缓存
        Cache localCache = localCacheManager.getCache(cacheName);
        if (localCache != null) {
            localCache.put(key, value);
            log.debug("存储数据到本地缓存: {}:{}", cacheName, key);
        }
        
        // 2. 存储到Redis缓存
        Cache redisCache = redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            redisCache.put(key, value);
            log.debug("存储数据到Redis缓存: {}:{}", cacheName, key);
        }
    }
    
    /**
     * 从缓存中删除数据
     * 同时从本地缓存和Redis缓存中删除
     *
     * @param cacheName 缓存名称
     * @param key 缓存键
     */
    public void evict(String cacheName, String key) {
        // 1. 从本地缓存删除
        Cache localCache = localCacheManager.getCache(cacheName);
        if (localCache != null) {
            localCache.evict(key);
            log.debug("从本地缓存删除数据: {}:{}", cacheName, key);
        }
        
        // 2. 从Redis缓存删除
        Cache redisCache = redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            redisCache.evict(key);
            log.debug("从Redis缓存删除数据: {}:{}", cacheName, key);
        }
    }
    
    /**
     * 清空缓存
     * 同时清空本地缓存和Redis缓存
     *
     * @param cacheName 缓存名称
     */
    public void clear(String cacheName) {
        // 1. 清空本地缓存
        Cache localCache = localCacheManager.getCache(cacheName);
        if (localCache != null) {
            localCache.clear();
            log.debug("清空本地缓存: {}", cacheName);
        }
        
        // 2. 清空Redis缓存
        Cache redisCache = redisCacheManager.getCache(cacheName);
        if (redisCache != null) {
            redisCache.clear();
            log.debug("清空Redis缓存: {}", cacheName);
        }
    }
}
