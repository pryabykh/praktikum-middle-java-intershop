package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.CartDto;

public interface CartItemService {

    void modifyCart(Long itemId, String cartAction);

    CartDto fetchCartItems();
}
