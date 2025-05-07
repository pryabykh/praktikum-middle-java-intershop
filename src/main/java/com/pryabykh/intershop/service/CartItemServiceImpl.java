package com.pryabykh.intershop.service;

import com.pryabykh.intershop.constant.CartActions;
import com.pryabykh.intershop.dto.CartDto;
import com.pryabykh.intershop.dto.ItemDto;
import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;

    private final UserService userService;
    private final CacheService cacheService;

    public CartItemServiceImpl(CartItemRepository cartItemRepository,
                               ItemRepository itemRepository,
                               UserService userService,
                               CacheService cacheService) {
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional
    public Mono<Void> modifyCart(Long itemId, String cartAction) {
        return userService.fetchDefaultUserId()
                .flatMap(userId -> cacheService.evictCaches(userId, itemId).then(Mono.just(userId)))
                .flatMap(userId -> cartItemRepository.findByItemIdAndUserId(itemId, userId)
                        .flatMap(cartItem -> {
                            if (CartActions.PLUS.equals(cartAction)) {
                                cartItem.setCount(cartItem.getCount() + 1);
                                return cartItemRepository.save(cartItem);
                            }
                            if (CartActions.MINUS.equals(cartAction)) {
                                if (cartItem.getCount() > 1) {
                                    cartItem.setCount(cartItem.getCount() - 1);
                                    return cartItemRepository.save(cartItem);
                                } else {
                                    return cartItemRepository.delete(cartItem);
                                }
                            }
                            if (CartActions.DELETE.equals(cartAction)) {
                                return cartItemRepository.delete(cartItem);
                            }
                            return Mono.empty();
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                            if (CartActions.PLUS.equals(cartAction)) {
                                CartItem newCartItem = new CartItem();
                                newCartItem.setUserId(userId);
                                newCartItem.setCount(1);
                                newCartItem.setItemId(itemId);
                                return cartItemRepository.save(newCartItem).then();
                            }
                            return Mono.empty();
                        }))
                )
                .then();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CartDto> fetchCartItems() {
        return userService.fetchDefaultUserId()
                .flatMapMany(cartItemRepository::findByUserIdOrderByIdDesc)
                .flatMap(cartItem -> itemRepository.findById(cartItem.getItemId())
                        .map(item -> {
                            long itemPrice = item.getPrice() * cartItem.getCount() / 100;
                            return new ItemDto(
                                    item.getId(),
                                    item.getTitle(),
                                    String.valueOf(itemPrice),
                                    item.getDescription(),
                                    String.valueOf(item.getImageId()),
                                    cartItem.getCount()
                            );
                        }))
                .collectList()
                .map(itemDtos -> {
                    long total = itemDtos.stream()
                            .mapToLong(dto -> Long.parseLong(dto.getPrice()))
                            .sum();
                    return new CartDto(itemDtos, total, itemDtos.isEmpty());
                });
    }
}
