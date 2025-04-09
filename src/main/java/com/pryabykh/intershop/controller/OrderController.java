package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/buy")
    public String createOrder(Model model) {
        orderService.createOrder();
        model.addAttribute("newOrder", true);
        return "order";
    }
}
