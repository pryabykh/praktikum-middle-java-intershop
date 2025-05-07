package com.pryabykh.intershop.service;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CacheServiceImpl implements CacheService {
    private final ReactiveStringRedisTemplate redisTemplate;

    public CacheServiceImpl(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Void> evictCaches(Long userId, Long itemId) {
        Flux<Long> deleteItemsList = redisTemplate.keys("itemsList::" + userId + "*").flatMap(redisTemplate::delete);
        Mono<Long> deleteItems = redisTemplate.delete("items::" + userId + "," + itemId);
        Mono<Long> deleteCartItems = redisTemplate.delete("cartItems::" + userId);
        return Mono.when(deleteItemsList, deleteItems, deleteCartItems);
    }
}
