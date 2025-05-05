package com.pryabykh.intershop.config;

import com.pryabykh.intershop.dto.ItemDto;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class RedisSerializationConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer weatherCacheCustomizer() {
        return builder -> builder.withCacheConfiguration(
                "item",                                         // Имя кеша
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.of(1, ChronoUnit.MINUTES))  // TTL
                        .serializeValuesWith(                          // Сериализация JSON
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new Jackson2JsonRedisSerializer<>(ItemDto.class)
                                )
                        )
        );
    }
}
