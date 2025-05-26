package com.pryabykh.intershop.service;

import com.pryabykh.intershop.client.BalanceApiClient;
import com.pryabykh.intershop.constant.CartActions;
import com.pryabykh.intershop.dto.BalanceDto;
import com.pryabykh.intershop.dto.CartDto;
import com.pryabykh.intershop.dto.ItemDto;
import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final BalanceApiClient balanceApi;
    private final UserService userService;
    private final CacheService cacheService;

    public CartItemServiceImpl(CartItemRepository cartItemRepository,
                               ItemRepository itemRepository,
                               BalanceApiClient balanceApi,
                               UserService userService,
                               CacheService cacheService) {
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
        this.balanceApi = balanceApi;
        this.userService = userService;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional
    public Mono<Void> modifyCart(Long itemId, String cartAction) {
        return userService.fetchCurrentUserId()
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
        return userService.fetchCurrentUserId().flatMap(userId -> {
            Mono<BalanceDto> balanceMono = balanceApi.balanceGet(userId)
                    .map(balanceResponse -> new BalanceDto(balanceResponse.getBalance(), true))
                    .onErrorResume(e -> Mono.just(new BalanceDto(null, false)));

            Mono<List<ItemDto>> monoItemsDto = cartItemRepository.findByUserIdOrderByIdDesc(userId)
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
                            })).collectList();

            return Mono.zip(balanceMono, monoItemsDto, (balance, itemDtos) -> {
                long total = itemDtos.stream()
                        .mapToLong(dto -> Long.parseLong(dto.getPrice()))
                        .sum();
                if (balance.isAvailable()) {
                    boolean possibleToBuy = (balance.getBalance() / 100 - total) >= 0;
                    return new CartDto(itemDtos, total, itemDtos.isEmpty(), possibleToBuy, true);
                } else {
                    return new CartDto(itemDtos, total, itemDtos.isEmpty(), false, false);
                }
            });
        });
    }
}
