package com.pryabykh.intershop.service;

import org.springframework.cache.annotation.CacheEvict;
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
        Mono<Long> deleteItems;
        if (itemId != null) {
            deleteItems = redisTemplate.delete("items::" + userId + "," + itemId);
        } else {
            deleteItems = redisTemplate.delete("items::" + userId + "*");
        }
        Mono<Long> deleteCartItems = redisTemplate.delete("cartItems::" + userId);
        return Mono.when(deleteItemsList, deleteItems, deleteCartItems);
    }

    @Override
    public Mono<Void> evictAllCaches() {
        Flux<Long> deleteItemsList = redisTemplate.keys("itemsList::*").flatMap(redisTemplate::delete);
        Flux<Long> deleteItems = redisTemplate.keys("items::*").flatMap(redisTemplate::delete);
        Flux<Long> deleteCartItems = redisTemplate.keys("cartItems::*").flatMap(redisTemplate::delete);
        return Mono.when(deleteItemsList, deleteItems, deleteCartItems);
    }

    @CacheEvict(
            value = "itemsList",
            allEntries = true
    )
    public Mono<Void> evictItemsCache() {
        return Mono.empty();
    }
}
