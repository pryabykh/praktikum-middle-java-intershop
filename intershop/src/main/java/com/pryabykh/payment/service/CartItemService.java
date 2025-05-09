package com.pryabykh.payment.service;

import com.pryabykh.payment.dto.CartDto;
import reactor.core.publisher.Mono;

public interface CartItemService {

    Mono<Void> modifyCart(Long itemId, String cartAction);

    Mono<CartDto> fetchCartItems();
}
