package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.CartDto;
import reactor.core.publisher.Mono;

public interface CartItemService {

    Mono<Void> modifyCart(Long itemId, String cartAction);

    Mono<CartDto> fetchCartItems();
}
