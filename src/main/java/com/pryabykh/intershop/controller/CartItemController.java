package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.dto.CartDto;
import com.pryabykh.intershop.service.CartItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String modifyCartAndRedirectToMain(@PathVariable("itemId") Long itemId,
                                              @RequestParam("action") String action) {
        cartItemService.modifyCart(itemId, action).subscribe();
        return "redirect:/main/items";
    }

    @PostMapping("/items/{itemId}")
    public String modifyCartAndRedirectToItem(@PathVariable("itemId") Long itemId,
                                              @RequestParam("action") String action) {
        cartItemService.modifyCart(itemId, action).subscribe();
        return "redirect:/items/" + itemId;
    }

    @PostMapping("/cart/items/{itemId}")
    public String modifyCartAndRedirectToCart(@PathVariable("itemId") Long itemId,
                                              @RequestParam("action") String action) {
        cartItemService.modifyCart(itemId, action).subscribe();
        return "redirect:/cart/items";
    }

    @GetMapping("/cart/items")
    public String fetchCartItems(Model model) {
        CartDto cartDto = cartItemService.fetchCartItems().block();
        model.addAttribute("items", cartDto.getItems());
        model.addAttribute("total", cartDto.getTotal());
        model.addAttribute("empty", cartDto.isEmpty());
        return "cart";
    }
}
