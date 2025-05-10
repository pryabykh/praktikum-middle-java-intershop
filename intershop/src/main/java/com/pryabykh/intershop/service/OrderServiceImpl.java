package com.pryabykh.intershop.service;

import com.pryabykh.intershop.client.PaymentApiClient;
import com.pryabykh.intershop.client.domain.PayPostRequest;
import com.pryabykh.intershop.dto.OrderDto;
import com.pryabykh.intershop.dto.OrderItemDto;
import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.entity.Order;
import com.pryabykh.intershop.entity.OrderItem;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.ItemRepository;
import com.pryabykh.intershop.repository.OrderItemRepository;
import com.pryabykh.intershop.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final PaymentApiClient paymentApiClient;
    private final CacheService cacheService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ItemRepository itemRepository,
                            CartItemRepository cartItemRepository,
                            OrderItemRepository orderItemRepository,
                            UserService userService,
                            PaymentApiClient paymentApiClient,
                            CacheService cacheService) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.userService = userService;
        this.paymentApiClient = paymentApiClient;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional
    public Mono<Long> createOrder() {
        Mono<Void> evictAllCachesMono = cacheService.evictAllCaches();
        Mono<Long> orderIdMono = userService.fetchDefaultUserId()
                .flatMapMany(cartItemRepository::findByUserIdOrderByIdDesc)
                .collectList()
                .flatMap(cartItems -> {
                    Order order = new Order();
                    List<Mono<OrderItem>> orderItemMonos = new ArrayList<>();
                    AtomicLong totalSum = new AtomicLong(0);

                    for (CartItem cartItem : cartItems) {
                        Mono<OrderItem> orderItemMono = itemRepository.findById(cartItem.getItemId())
                                .map(item -> {
                                    OrderItem orderItem = new OrderItem();
                                    orderItem.setCount(cartItem.getCount());
                                    orderItem.setDescription(item.getDescription());
                                    orderItem.setTitle(item.getTitle());
                                    orderItem.setImageId(item.getImageId());
                                    orderItem.setPrice(item.getPrice());

                                    long itemTotal = item.getPrice() * cartItem.getCount();
                                    totalSum.addAndGet(itemTotal);

                                    return orderItem;
                                });

                        orderItemMonos.add(orderItemMono);
                    }

                    return Flux.concat(orderItemMonos)
                            .collectList()
                            .flatMap(orderItems -> {
                                order.setUserId(cartItems.isEmpty() ? null : cartItems.get(0).getUserId());
                                order.setTotalSum(totalSum.get());

                                PayPostRequest payRequest = new PayPostRequest().amount(totalSum.get()).userId(order.getUserId());
                                return paymentApiClient.payPost(payRequest).flatMap(payResponse -> {
                                    if (payResponse.getSuccess()) {
                                        return orderRepository.save(order)
                                                .flatMap(savedOrder -> {
                                                    List<Mono<OrderItem>> savedOrderItemMonos = orderItems.stream()
                                                            .map(orderItem -> {
                                                                orderItem.setOrderId(savedOrder.getId());
                                                                Mono<Void> evictCaches = cacheService.evictCaches(order.getUserId(), null);
                                                                Mono<OrderItem> saveOrderItemMono = orderItemRepository.save(orderItem);
                                                                return evictCaches.then(saveOrderItemMono);
                                                            })
                                                            .toList();

                                                    return Flux.concat(savedOrderItemMonos)
                                                            .then(cartItemRepository.deleteByUserId(savedOrder.getUserId()))
                                                            .thenReturn(savedOrder.getId());
                                                });
                                    } else {
                                        return Mono.error(new IllegalAccessError());
                                    }
                                });
                            });
                });
        return evictAllCachesMono.then(orderIdMono);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OrderDto> findById(Long orderId) {
        return orderRepository.findById(orderId)
                .flatMap(this::mapOrder)
                .switchIfEmpty(Mono.error(new RuntimeException()));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrderDto> findAll() {
        return userService.fetchDefaultUserId()
                .flatMapMany(orderRepository::findByUserIdOrderByIdDesc)
                .flatMap(this::mapOrder);
    }

    private Mono<OrderDto> mapOrder(Order order) {
        return orderItemRepository.findByOrderId(order.getId())
                .map(item -> new OrderItemDto(
                        item.getId(),
                        item.getTitle(),
                        String.valueOf(item.getPrice() * item.getCount() / 100),
                        String.valueOf(item.getImageId()),
                        item.getCount()
                ))
                .collectList()
                .map(orderItems -> new OrderDto(
                        order.getId(),
                        order.getTotalSum() / 100,
                        orderItems
                ));
    }
}
