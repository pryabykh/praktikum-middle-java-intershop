package com.pryabykh.intershop.service;

import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.entity.Order;
import com.pryabykh.intershop.entity.OrderItem;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartItemRepository cartItemRepository,
                            UserService userService) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void createOrder() {
        Long currentUserId = userService.fetchDefaultUserId();
        List<CartItem> cartItems = cartItemRepository.findByUserId(currentUserId);
        Order order = new Order();
        AtomicLong totalSum = new AtomicLong();
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setCount(cartItem.getCount());
            orderItem.setPrice(cartItem.getItem().getPrice());
            orderItem.setDescription(cartItem.getItem().getDescription());
            orderItem.setTitle(cartItem.getItem().getTitle());
            orderItem.setImageId(cartItem.getItem().getImageId());
            totalSum.addAndGet(cartItem.getItem().getPrice() * cartItem.getCount());
            return orderItem;
        }).toList();
        order.setOrderItems(orderItems);
        order.setUserId(currentUserId);
        order.setTotalSum(totalSum.get());
        orderRepository.save(order);
        cartItemRepository.deleteByUserId(currentUserId);
    }
}
