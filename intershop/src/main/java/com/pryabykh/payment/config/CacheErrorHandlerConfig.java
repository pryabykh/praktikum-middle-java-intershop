package com.pryabykh.payment.config;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheErrorHandlerConfig implements CachingConfigurer {

    @Override
    public CacheErrorHandler errorHandler() {
        return new RelaxedCacheErrorHandler();
    }
}
