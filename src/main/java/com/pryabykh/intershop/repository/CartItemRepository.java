package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.entity.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByItemIdAndUserId(Long itemId, Long userId);

    List<CartItem> findByItemIdInAndUserId(List<Long> itemIds, Long userId);

    @EntityGraph(value = "cart-item-with-item-itself-entity-graph")
    List<CartItem> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
