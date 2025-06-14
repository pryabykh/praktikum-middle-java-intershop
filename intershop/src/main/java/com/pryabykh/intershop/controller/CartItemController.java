package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.service.CartItemService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import static com.pryabykh.intershop.utils.SecurityUtils.fetchUserRole;

@Controller
@RequestMapping("/")
public class CartItemController {
    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/modify/main/items/{itemId}")
    public Mono<String> modifyCartAndRedirectToMain(@PathVariable("itemId") Long itemId,
                                                    @RequestParam("action") String action) {
        return cartItemService.modifyCart(itemId, action)
                .then(Mono.just("redirect:/main/items"));
    }

    @GetMapping("/modify/items/{itemId}")
    public Mono<String> modifyCartAndRedirectToItem(@PathVariable("itemId") Long itemId,
                                                    @RequestParam("action") String action) {
        return cartItemService.modifyCart(itemId, action)
                .then(Mono.just("redirect:/items/" + itemId));
    }

    @GetMapping("/modify/cart/items/{itemId}")
    public Mono<String> modifyCartAndRedirectToCart(@PathVariable("itemId") Long itemId,
                                                    @RequestParam("action") String action) {
        return cartItemService.modifyCart(itemId, action)
                .then(Mono.just("redirect:/cart/items"));
    }

    @GetMapping("/cart/items")
    public Mono<Rendering> fetchCartItems(Authentication authentication) {
        return cartItemService.fetchCartItems()
                .map(cartDto -> Rendering.view("cart")
                        .modelAttribute("items", cartDto.getItems())
                        .modelAttribute("total", cartDto.getTotal())
                        .modelAttribute("empty", cartDto.isEmpty())
                        .modelAttribute("possibleToBuy", cartDto.isPossibleToBuy())
                        .modelAttribute("available", cartDto.isAvailable())
                        .modelAttribute("role", fetchUserRole(authentication))
                        .build());
    }
}
