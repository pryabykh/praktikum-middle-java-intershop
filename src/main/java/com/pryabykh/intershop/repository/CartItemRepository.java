package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.entity.CartItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface CartItemRepository extends R2dbcRepository<CartItem, Long> {

    Mono<CartItem> findByItemIdAndUserId(Long itemId, Long userId);

    Flux<CartItem> findByItemIdInAndUserId(List<Long> itemIds, Long userId);

    Flux<CartItem> findByUserIdOrderByIdDesc(Long userId);

    Mono<Void> deleteByUserId(Long userId);
}
