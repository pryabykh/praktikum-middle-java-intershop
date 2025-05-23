package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.OrderDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

    Mono<Long> createOrder();

    Mono<OrderDto> findById(Long orderId);

    Flux<OrderDto> findAll();
}
