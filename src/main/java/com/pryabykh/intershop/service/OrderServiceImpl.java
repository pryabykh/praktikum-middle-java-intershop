package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.OrderDto;
import com.pryabykh.intershop.dto.OrderItemDto;
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
    public Long createOrder() {
        Long currentUserId = userService.fetchDefaultUserId();
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByIdDesc(currentUserId);
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
        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteByUserId(currentUserId);
        return savedOrder.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto findById(Long orderId) {
        return orderRepository.findById(orderId).map(this::mapOrder).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> findAll() {
        Long currentUserId = userService.fetchDefaultUserId();
        return orderRepository.findByUserIdOrderByIdDesc(currentUserId)
                .stream()
                .map(this::mapOrder)
                .toList();
    }

    private OrderDto mapOrder(Order order) {
        return new OrderDto(
                order.getId(),
                order.getTotalSum() / 100,
                order.getOrderItems().stream().map(item -> {
                    return new OrderItemDto(
                            item.getId(),
                            item.getTitle(),
                            String.valueOf(item.getPrice() * item.getCount() / 100),
                            String.valueOf(item.getImageId()),
                            item.getCount()
                    );
                }).toList()
        );
    }
}
