package com.pryabykh.payment.service;

import reactor.core.publisher.Mono;

public interface CacheService {

    Mono<Void> evictCaches(Long userId, Long itemId);

    Mono<Void> evictItemsCache();
}
