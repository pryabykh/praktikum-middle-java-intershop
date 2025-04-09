package com.pryabykh.intershop.service;

import com.pryabykh.intershop.constant.CartActions;
import com.pryabykh.intershop.dto.CartDto;
import com.pryabykh.intershop.dto.ItemDto;
import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;

    private final UserService userService;

    public CartItemServiceImpl(CartItemRepository cartItemRepository, UserService userService) {
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
    }

    @Override
    @Transactional
    public void modifyCart(Long itemId, String cartAction) {
        Long currentUserId = userService.fetchDefaultUserId();
        cartItemRepository.findByItemIdAndUserId(itemId, currentUserId).ifPresentOrElse(cartItem -> {
            if (CartActions.PLUS.equals(cartAction)) {
                cartItem.setCount(cartItem.getCount() + 1);
                cartItemRepository.save(cartItem);
            }
            if (CartActions.MINUS.equals(cartAction)) {
                if (cartItem.getCount() > 1) {
                    cartItem.setCount(cartItem.getCount() - 1);
                    cartItemRepository.save(cartItem);
                } else {
                    cartItemRepository.delete(cartItem);
                }
            }
            if (CartActions.DELETE.equals(cartAction)) {
                cartItemRepository.delete(cartItem);
            }
        }, () -> {
            if (CartActions.PLUS.equals(cartAction)) {
                cartItemRepository.save(new CartItem(currentUserId, 1, new Item(itemId)));
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto fetchCartItems() {
        Long currentUserId = userService.fetchDefaultUserId();
        AtomicLong total = new AtomicLong();
        List<ItemDto> items = cartItemRepository.findByUserId(currentUserId).stream().map(cartItem -> {
            long itemPrice = cartItem.getItem().getPrice() * cartItem.getCount() / 100;
            total.addAndGet(itemPrice);
            return new ItemDto(
                    cartItem.getItem().getId(),
                    cartItem.getItem().getTitle(),
                    String.valueOf(itemPrice),
                    cartItem.getItem().getDescription(),
                    String.valueOf(cartItem.getItem().getImageId()),
                    cartItem.getCount()
            );
        }).toList();
        return new CartDto(items, total.get(), items.isEmpty());
    }
}
