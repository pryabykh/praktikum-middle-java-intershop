package com.pryabykh.payment.service;

import reactor.core.publisher.Mono;

public interface UserService {

    Mono<Long> fetchDefaultUserId();
}
