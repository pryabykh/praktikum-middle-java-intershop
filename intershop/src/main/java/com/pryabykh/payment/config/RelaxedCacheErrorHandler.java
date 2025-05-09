package com.pryabykh.payment.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;

@Component
public class RelaxedCacheErrorHandler implements CacheErrorHandler {
    private static Logger logger = LogManager.getLogger(RelaxedCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        logger.warn("Redis GET failed - ignoring cache. Error: " + exception.getMessage());
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        logger.warn("Redis PUT failed - ignoring cache. Error: " + exception.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        logger.warn("Redis EVICT failed - ignoring cache. Error: " + exception.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        logger.warn("Redis CLEAR failed - ignoring cache. Error: " + exception.getMessage());
    }
}
