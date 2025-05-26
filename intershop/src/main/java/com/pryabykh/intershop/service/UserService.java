package com.pryabykh.intershop.service;

import reactor.core.publisher.Mono;

public interface UserService {

    Mono<Long> fetchCurrentUserId();
}
