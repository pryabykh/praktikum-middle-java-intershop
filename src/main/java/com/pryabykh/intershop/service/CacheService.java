package com.pryabykh.intershop.service;

import reactor.core.publisher.Mono;

public interface CacheService {

    Mono<Void> evictCaches(Long userId, Long itemId);
}
