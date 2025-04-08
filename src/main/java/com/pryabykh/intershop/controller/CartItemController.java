package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.service.CartItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class CartItemController {
    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping("/main/items/{itemId}")
    public String mainItems(@PathVariable("itemId") Long itemId,
                            @RequestParam("action") String action) {
        cartItemService.modifyCart(itemId, action);
        return "redirect:/main/items";
    }
}
