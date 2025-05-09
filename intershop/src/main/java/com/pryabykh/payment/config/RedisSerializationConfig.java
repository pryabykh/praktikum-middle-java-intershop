package com.pryabykh.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pryabykh.payment.entity.Image;
import com.pryabykh.payment.entity.Item;
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
    public RedisCacheManagerBuilderCustomizer weatherCacheCustomizer(ObjectMapper objectMapper) {
        return builder -> builder.withCacheConfiguration(
                        "items",                                         // Имя кеша
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.of(5, ChronoUnit.MINUTES))  // TTL
                                .serializeValuesWith(                          // Сериализация JSON
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new Jackson2JsonRedisSerializer<>(Item.class)
                                        )
                                )
                )
                .withCacheConfiguration(
                        "itemsList",                                         // Имя кеша
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.of(5, ChronoUnit.MINUTES))  // TTL
                                .serializeValuesWith(                          // Сериализация JSON
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new ItemListRedisSerializer(objectMapper)
                                        )
                                )
                )
                .withCacheConfiguration(
                        "images",                                         // Имя кеша
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.of(60, ChronoUnit.MINUTES))  // TTL
                                .serializeValuesWith(                          // Сериализация JSON
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new Jackson2JsonRedisSerializer<>(Image.class)
                                        )
                                )
                )
                .withCacheConfiguration(
                        "cartItems",                                         // Имя кеша
                        RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.of(5, ChronoUnit.MINUTES))  // TTL
                                .serializeValuesWith(                          // Сериализация JSON
                                        RedisSerializationContext.SerializationPair.fromSerializer(
                                                new CartItemListRedisSerializer(objectMapper)
                                        )
                                )
                );
    }
}
