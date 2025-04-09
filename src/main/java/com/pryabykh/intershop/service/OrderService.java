package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.OrderDto;

public interface OrderService {

    Long createOrder();

    OrderDto findById(Long orderId);
}
