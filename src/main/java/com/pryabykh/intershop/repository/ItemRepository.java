package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.entity.Item;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {

    @Query("SELECT distinct i.*, c.count FROM intershop.items i " +
            "left join intershop.carts c on c.item_id = i.id and c.user_id = :userId" +
            " WHERE  LOWER(i.title) " +
            "LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY i.id desc LIMIT :limit OFFSET :offset")
    Flux<Item> findAllByNameLikeOrderByIdDesc(Long userId, String name, int limit, int offset);

    @Query("SELECT distinct i.*, c.count FROM intershop.items i " +
            "left join intershop.carts c on c.item_id = i.id and c.user_id = :userId" +
            " WHERE  LOWER(i.title) " +
            "LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY i.title LIMIT :limit OFFSET :offset")
    Flux<Item> findAllByNameLikeOrderByTitleAsc(Long userId, String name, int limit, int offset);

    @Query("SELECT distinct i.*, c.count FROM intershop.items i " +
            "left join intershop.carts c on c.item_id = i.id and c.user_id = :userId" +
            " WHERE  LOWER(i.title) " +
            "LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY i.price LIMIT :limit OFFSET :offset")
    Flux<Item> findAllByNameLikeOrderByPriceAsc(Long userId, String name, int limit, int offset);

    @Query("SELECT COUNT(*) FROM intershop.items WHERE LOWER(title) LIKE LOWER(CONCAT('%', :name, '%'))")
    Mono<Long> countByNameLike(String name);

    @Query("SELECT distinct i.*, c.count FROM intershop.items i " +
            "left join intershop.carts c on c.item_id = i.id and c.user_id = :userId " +
            "ORDER BY i.id desc LIMIT :limit OFFSET :offset")
    Flux<Item> findAllOrderByIdDesc(Long userId, int limit, int offset);

    @Query("SELECT distinct i.*, c.count FROM intershop.items i " +
            "left join intershop.carts c on c.item_id = i.id and c.user_id = :userId " +
            "ORDER BY i.title LIMIT :limit OFFSET :offset")
    Flux<Item> findAllOrderByTitleAsc(Long userId, int limit, int offset);

    @Query("SELECT distinct i.*, c.count FROM intershop.items i " +
            "left join intershop.carts c on c.item_id = i.id and c.user_id = :userId " +
            "ORDER BY i.price LIMIT :limit OFFSET :offset")
    Flux<Item> findAllOrderByPriceAsc(Long userId, int limit, int offset);

    @Query("SELECT COUNT(*) FROM intershop.items")
    Mono<Long> count();

    @Query("SELECT distinct i.*, c.count FROM intershop.items i " +
            "left join intershop.carts c on c.item_id = i.id and c.user_id = :userId where i.id = :itemId")
    Mono<Item> findItemById(Long userId, Long itemId);
}
