package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.dto.OrderDto;
import com.pryabykh.intershop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/buy")
    public String createOrder() {
        Long orderId = orderService.createOrder().block();
        return "redirect:/orders/" + orderId + "?newOrder=true";
    }

    @GetMapping("/orders/{id}")
    public String fetchOrder(@PathVariable("id") Long id,
                             @RequestParam(value = "newOrder", defaultValue = "false") boolean newOrder,
                             Model model) {
        OrderDto order = orderService.findById(id).block();
        model.addAttribute("newOrder", newOrder);
        model.addAttribute("order", order);
        return "order";
    }

    @GetMapping("/orders")
    public String fetchOrders(Model model) {
        List<OrderDto> orders = orderService.findAll().collectList().block();
        model.addAttribute("orders", orders);
        return "orders";
    }
}
