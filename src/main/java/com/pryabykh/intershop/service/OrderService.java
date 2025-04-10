package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.OrderDto;

import java.util.List;

public interface OrderService {

    Long createOrder();

    OrderDto findById(Long orderId);

    List<OrderDto> findAll();
}
