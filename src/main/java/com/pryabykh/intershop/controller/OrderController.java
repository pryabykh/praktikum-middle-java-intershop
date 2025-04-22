package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/buy")
    public Mono<String> createOrder() {
        return orderService.createOrder()
                .map(orderId -> "redirect:/orders/" + orderId + "?newOrder=true");
    }

    @GetMapping("/orders/{id}")
    public Mono<Rendering> fetchOrder(@PathVariable("id") Long id,
                                      @RequestParam(value = "newOrder", defaultValue = "false") boolean newOrder) {
        return orderService.findById(id)
                .map(order -> Rendering.view("order")
                        .modelAttribute("newOrder", newOrder)
                        .modelAttribute("order", order)
                        .build());
    }

    @GetMapping("/orders")
    public Mono<Rendering> fetchOrders() {
        return orderService.findAll()
                .collectList()
                .map(orders -> Rendering.view("orders")
                        .modelAttribute("orders", orders)
                        .build());
    }
}